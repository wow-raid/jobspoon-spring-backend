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
    private final UserWordbookTermRepository userWordbookTermRepository;
    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserRecentTermService userRecentTermService;
    private final RequestContextFilter requestContextFilter;
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
    @PostMapping("/user-terms/favorites")
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
    @DeleteMapping("/user-terms/favorites/{favoriteTermId}")
    public ResponseEntity<?> deleteFavoriteTerm(@PathVariable Long favoriteTermId) {
        log.info("[favorite:delete] favoriteTermId={}", favoriteTermId);
        return favoriteTermService.deleteFavoriteTerm(favoriteTermId);
    }

    // 단어장 폴더 추가
    @PostMapping("/user-terms/folders")
    public CreateUserWordbookFolderResponseForm createFolder(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateUserWordbookFolderRequestForm requestForm) {

        final String token = extractBearer(authorizationHeader);
        final String tokenPrefix = token.length() >= 8 ? token.substring(0, 8) : token;
        Long accountId;
        try {
            accountId = redisCacheService.getValueByKey(token, Long.class);
        } catch (Exception e) {
            log.error("[folder:create] Redis error tokenPrefix={}... : {}", tokenPrefix, e.toString(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (accountId == null) {
            log.warn("[folder:create] No redis mapping tokenPrefix={}..., 401", tokenPrefix);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

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

    // 암기 상태 변경 : userTermId 기준
    @PatchMapping("/me/user-terms/{userTermId}/memorization")
    public UpdateMemorizationResponseForm updateMemorizationByUserTermId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long userTermId,
            @RequestBody @Valid UpdateMemorizationRequestForm requestForm
    ) {
        Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[memo:update:byUserTerm] accountId={}, userTermId={}, status={}",
                accountId, userTermId, requestForm.getStatus());

        UserWordbookTerm uwt = userWordbookTermRepository.findById(userTermId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "선택하신 용어를 찾을 수 없습니다."));

        UpdateMemorizationRequest request =
                requestForm.toUpdateMemorizationRequest(accountId, uwt.getTerm().getId());
        UpdateMemorizationResponse response = memorizationService.updateMemorization(request);
        log.debug("[memo:update:byUserTerm] response={}", response);
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
    @PostMapping("/terms/{termId}/view")
    public ResponseEntity<RecordTermViewResponseForm> view(
            @RequestHeader("Authorization") String AuthorizationHeader,
            @PathVariable Long termId,
            @RequestBody @Valid RecordTermViewRequestForm requestForm) {
        Long accountId = accountIdFromAuth(AuthorizationHeader);
        log.info("[recent:view] accountId={}, termId={}, reqForm={}", accountId, termId, requestForm);
        RecordTermViewRequest request = requestForm.toRecordTermViewRequest(accountId, termId);
        RecordTermViewResponse response = userRecentTermService.recordTermView(request);
        log.debug("[recent:view] response={}", response);
        return ResponseEntity.ok(RecordTermViewResponseForm.from(response));
    }

    // 인증된 사용자가 폴더별로 단어장 조회하기
    @GetMapping("/folders/{folderId}/terms")
    public ListUserWordbookTermResponseForm userTermList(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long folderId,
            @ModelAttribute ListUserWordbookTermRequestForm requestForm
    ) {
        final Long accountId = accountIdFromAuth(authorizationHeader);
        log.info("[folder:terms:list:req] accountId={}, folderId={}, form={}", accountId, folderId, requestForm);
        try {
            ListUserWordbookTermRequest request = requestForm.toListUserTermRequest(accountId, folderId);
            ListUserWordbookTermResponse response = userWordbookFolderService.list(request);

            int size = (response == null || response.getUserWordbookTermList() == null)
                    ? 0 : response.getUserWordbookTermList().size();
            log.info("[folder:terms:list:res] accountId={}, folderId={}, size={}, totalPages={}, totalItems={}",
                    accountId, folderId, size, response.getTotalPages(), response.getTotalItems());

            return ListUserWordbookTermResponseForm.from(response);
        } catch (Exception e) {
            log.error("[folder:terms:list:err] accountId={}, folderId={}, form={}, ex={}",
                    accountId, folderId, requestForm, e.toString(), e);
            throw e; // 전역 핸들러가 잡아서 JSON으로 내려가게 (2번 참고)
        }
    }


    // 단어장 폴더 순서 변경하기
    @PatchMapping("/user-terms/folders/reorder")
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
    @GetMapping("/user-terms/folders")
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
    @PostMapping("/user-terms/folders/{folderId}/terms")
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
}
