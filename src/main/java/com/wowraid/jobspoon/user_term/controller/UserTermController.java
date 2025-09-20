package com.wowraid.jobspoon.user_term.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.user_term.controller.request_form.*;
import com.wowraid.jobspoon.user_term.controller.response_form.*;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import com.wowraid.jobspoon.user_term.repository.UserTermProgressRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.FavoriteTermService;
import com.wowraid.jobspoon.user_term.service.MemorizationService;
import com.wowraid.jobspoon.user_term.service.UserRecentTermService;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderService;
import com.wowraid.jobspoon.user_term.service.request.*;
import com.wowraid.jobspoon.user_term.service.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserTermController {

    private final FavoriteTermService favoriteTermService;
    private final UserWordbookFolderService userWordbookFolderService;
    private final MemorizationService memorizationService;
    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserRecentTermService userRecentTermService;
    private final RedisCacheService redisCacheService;
    private final UserTermProgressRepository userTermProgressRepository;

    // Authorization 헤더에서 accountId 복원 (Redis 매핑 기반)
    private Long accountIdFromAuth(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            log.warn("[auth] Authorization header missing/blank");
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        final String token = authorizationHeader.startsWith("Bearer")
                ? authorizationHeader.substring(7).trim()
                : authorizationHeader.trim();

        final String tokenPrefix = token.length() >= 8 ? token.substring(0, 8) : token;
        log.debug("[auth] Bearer token received. len={}, prefix={}...", token.length(), tokenPrefix);

        try {
            Long accountId = redisCacheService.getValueByKey(token, Long.class);
            if (accountId == null) {
                log.warn("[auth] Redis map not found for tokenPrefix={}..., returning 401", tokenPrefix);
                throw new ResponseStatusException(UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            }
            log.info("[auth] tokenPrefix={}... -> accountId={}", tokenPrefix, accountId);
            return accountId;
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            // Redis 인증/연결 문제 등 모든 예외를 401로 변환하고 로그 남김
            log.error("[auth] Redis access failed for tokenPrefix={}... : {}", tokenPrefix, e.toString(), e);
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    // 즐겨찾기 용어 등록
    @PostMapping("/me/favorite-terms")
    public CreateFavoriteTermResponseForm responseForm(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateFavoriteTermRequestForm requestForm) {
        log.info("[favorite:create] reqForm={}", requestForm);
        Long accountId = accountIdFromAuth(authorizationHeader);
        CreateFavoriteTermRequest request = requestForm.toCreateFavoriteTermRequest(accountId);
        CreateFavoriteTermResponse response = favoriteTermService.registerFavoriteTerm(request);
        log.debug("[favorite:create] accountId={} -> response={}", accountId, response);
        return CreateFavoriteTermResponseForm.from(response);
    }

    // 즐겨찾기 용어 삭제
    @DeleteMapping("/me/favorite-terms/{favoriteTermId}")
    public ResponseEntity<?> deleteFavoriteTerm(@PathVariable Long favoriteTermId) {
        log.info("[favorite:delete] favoriteTermId={}", favoriteTermId);
        return favoriteTermService.deleteFavoriteTerm(favoriteTermId);
    }

    // 즐겨찾기 용어 이동
    @PatchMapping("/me/wordbook/favorites:move")
    public MoveFavoritesResponseForm moveFavorites(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid MoveFavoritesRequestForm requestForm) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        var request = requestForm.toRequest(accountId);
        var response = favoriteTermService.moveToFolder(request);
        return MoveFavoritesResponseForm.from(response);
    }

    // 폴더 간 이동 지원
    @PatchMapping("/me/folders/{sourceFolderId}/terms:move")
    public MoveFolderTermsResponseForm moveFolderTerms(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long sourceFolderId,
            @RequestBody @Valid MoveFolderTermsRequestForm requestForm) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        var res = userWordbookFolderService.moveTerms(accountId, sourceFolderId, requestForm.getTargetFolderId(), requestForm.getTermIds());
        return MoveFolderTermsResponseForm.from(res);
    }

    // 단어장 폴더 추가
    @PostMapping("/me/folders")
    public CreateUserWordbookFolderResponseForm createFolder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateUserWordbookFolderRequestForm requestForm) {

        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:create] accountId={}, reqForm={}", accountId, requestForm);

        CreateUserWordbookFolderRequest req = requestForm.toCreateFolderRequest(accountId);
        CreateUserWordbookFolderResponse res = userWordbookFolderService.registerWordbookFolder(req);
        log.debug("[folder:create] res={}", res);
        return CreateUserWordbookFolderResponseForm.from(res);
    }

    private static String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer "))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없습니다.");
        return header.substring(7).trim();
    }

    // 암기 상태 변경 : 로그인 사용자 기준, termId로 직접 상태 변경
    @PatchMapping("/me/terms/{termId}/memorization")
    public UpdateMemorizationResponseForm updateMemorizationByTermId(
            @RequestHeader("Authorization") String AuthorizationHeader,
            @PathVariable Long termId,
            @RequestBody @Valid UpdateMemorizationRequestForm requestForm
    ) {
        Long accountId = accountIdFromAuth(AuthorizationHeader);
        log.info("[memo:update:byTerm] accountId={}, termId={}, status={}",
                accountId, termId, requestForm.getStatus());

        UpdateMemorizationRequest request = requestForm.toUpdateMemorizationRequest(accountId, termId);
        UpdateMemorizationResponse response = memorizationService.updateMemorization(request);
        log.debug("[memo:update:byTerm] response={}", response);
        return UpdateMemorizationResponseForm.from(response);
    }

    @GetMapping("/me/terms/memorization")
    public Map<String, String> getMemorizationStatuses(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "ids") String idsCsv
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);

        // 빈/공백 방어
        if (idsCsv == null || idsCsv.isBlank()) {
            return Map.of();
        }

        // CSV -> List<Long>
        List<Long> termIds;
        try {
            termIds = Arrays.stream(idsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ids 파라미터 형식이 올바르지 않습니다.");
        }

        if (termIds.isEmpty()) return Map.of();

        var rows = userTermProgressRepository.findByIdAccountIdAndIdTermIdIn(accountId, termIds);

        // 기본값 LEARNING으로 채워 두고, 조회된 건 덮어쓰기
        Map<String, String> result = new LinkedHashMap<>();
        termIds.stream().distinct().forEach(id -> result.put(String.valueOf(id), "LEARNING"));
        rows.forEach(p -> result.put(String.valueOf(p.getId().getTermId()), p.getStatus().name()));
        return result;
    }

    // 최근 학습/열람 이벤트 발생 시 ‘최근 본 용어’로 저장하기
    @PostMapping("/me/terms/{termId}/view")
    public ResponseEntity<RecordTermViewResponseForm> view(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long termId,
            @RequestBody @Valid RecordTermViewRequestForm requestForm) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[recent:view] accountId={}, termId={}, reqForm={}", accountId, termId, requestForm);
        RecordTermViewRequest request = requestForm.toRecordTermViewRequest(accountId, termId);
        RecordTermViewResponse response = userRecentTermService.recordTermView(request);
        log.debug("[recent:view] response={}", response);
        return ResponseEntity.ok(RecordTermViewResponseForm.from(response));
    }

    // 인증된 사용자가 폴더별로 단어장 조회하기
    @GetMapping({"/me/folders/{folderId}/terms", "/folders/{folderId}/terms"})
    public ListUserWordbookTermResponseForm userTermList(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long folderId,
            @ModelAttribute ListUserWordbookTermRequestForm requestForm
    ) {
        final Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:terms:list:req] accountId={}, folderId={}, form={}", accountId, folderId, requestForm);

        // 존재 & 소유 검증
        var folderOpt = userWordbookFolderRepository.findById(folderId);
        if (folderOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다.");
        }
        var folder = folderOpt.get();
        if (!folder.getAccount().getId().equals(accountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        try {
            var req = requestForm.toListUserTermRequest(accountId, folderId);
            var res = userWordbookFolderService.list(req);
            return ListUserWordbookTermResponseForm.from(res);
        } catch (ResponseStatusException rse) {
            throw rse; // 서비스가 이미 상태코드를 정한 경우 유지
        } catch (Exception e) {
            log.error("[folder:terms:list:err] accountId={}, folderId={}, ex={}", accountId, folderId, e.toString(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
        }
    }

    // 단어장 폴더 순서 변경하기
    @PatchMapping("/me/folders:reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorderFolders(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid ReorderUserWordbookFoldersRequestForm requestForm
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:reorder] accountId={}, req={}", accountId, requestForm);
        userWordbookFolderService.reorder(requestForm.toRequest(accountId));
        log.debug("[folder:reorder] accountId={} done", accountId);
    }

    // 단어장 폴더 리스트 조회하기
    @GetMapping({"/me/folders", "/user-terms/folders"})
    public List<Map<String, Object>> listFolders(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:list] accountId={}", accountId);
        var list = userWordbookFolderRepository.findAllByAccount_IdOrderBySortOrderAscIdAsc(accountId);
        var result = list.stream()
                .map(f -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", f.getId());
                    m.put("folderName", f.getFolderName());
                    m.put("sortOrder", f.getSortOrder());
                    return m;
                })
                .collect(Collectors.toList());
        log.debug("[folder:list] accountId={}, count={}", accountId, result.size());
        return result;
    }

    // 단어장 폴더에 용어 추가하기
    @PostMapping("/me/folders/{folderId}/terms")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserWordbookTermResponseForm addTermFolder(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long folderId,
            @RequestBody @Valid AddTermToFolderRequestForm requestForm
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:attach] accountId={}, folderId={}, reqForm={}", accountId, folderId, requestForm);
        CreateUserWordbookTermRequest request = requestForm.toRequest(accountId, folderId);
        CreateUserWordbookTermResponse response = userWordbookFolderService.attachTerm(request);
        log.debug("[folder:attach] accountId={}, folderId={}, response={}", accountId, folderId, response);
        return CreateUserWordbookTermResponseForm.from(response);
    }

    // 단어장 폴더 이름 변경하기
    @PatchMapping("/me/folders/{folderId}")
    public RenameUserWordbookFolderResponseForm renameFolder(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long folderId,
            @RequestBody @Valid RenameUserWordbookFolderRequestForm requestForm) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:rename] accountId={}, folderId={}", accountId, folderId);

        var request = requestForm.toRequest(accountId, folderId);
        var response = userWordbookFolderService.rename(request);
        log.debug("[folder:rename] response={}", response);
        return RenameUserWordbookFolderResponseForm.from(response);
    }

    // 단어장 폴더 삭제(단건)
    @DeleteMapping("/me/folders/{folderId}")
    public ResponseEntity<Void> deleteFolder(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long folderId,
            @RequestParam(name = "mode", defaultValue = "purge") String mode,
            @RequestParam(name = "targetFolderId", required = false) Long targetFolderId
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        userWordbookFolderService.deleteOne(
                accountId,
                UserWordbookFolderService.DeleteMode.of(mode),
                folderId,
                targetFolderId
        );
        return ResponseEntity.noContent().build();
    }

    // 단어장 폴더 삭제(다건)
    @DeleteMapping("/me/folders:bulk")
    public ResponseEntity<Void> deleteFoldersBulk(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "mode", defaultValue = "purge") String mode,
            @RequestParam(name = "targetFolderId", required = false) Long targetFolderId,
            @RequestBody @Valid BulkDeleteFoldersRequestForm form
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);

        var ids = form.getFolderIds();
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        userWordbookFolderService.deleteBulk(
                accountId,
                UserWordbookFolderService.DeleteMode.of(mode),
                ids.stream().distinct().toList(),
                targetFolderId
        );
        return ResponseEntity.noContent().build();
    }

}