package com.wowraid.jobspoon.user_term.controller;

import com.wowraid.jobspoon.user_term.controller.request_form.*;
import com.wowraid.jobspoon.user_term.controller.response_form.*;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserTermController {

    private final FavoriteTermService favoriteTermService;
    private final UserWordbookFolderService userWordbookFolderService;
    private final MemorizationService memorizationService;
    private final UserWordbookTermRepository userWordbookTermRepository;
    private final UserRecentTermService userRecentTermService;
    private final RequestContextFilter requestContextFilter;

    // 즐겨찾기 용어 등록
    @PostMapping("/user-terms/favorites")
    public CreateFavoriteTermResponseForm responseForm (@RequestBody CreateFavoriteTermRequestForm requestForm) {
        log.info("Received request for new favorite term: {}", requestForm);
        CreateFavoriteTermRequest request = requestForm.toCreateFavoriteTermRequest();
        CreateFavoriteTermResponse response = favoriteTermService.registerFavoriteTerm(request);
        return CreateFavoriteTermResponseForm.from(response);
    }

    // 즐겨찾기 용어 삭제
    @DeleteMapping("/user-terms/favorites/{favoriteTermId}")
    public ResponseEntity<?> deleteFavoriteTerm(@PathVariable Long favoriteTermId) {
        log.info("Received request for delete favorite term: {}", favoriteTermId);
        return favoriteTermService.deleteFavoriteTerm(favoriteTermId);
    }

    // 단어장 폴더 추가
    @PostMapping("/user-terms/folders")
    public CreateUserWordbookFolderResponseForm responseForm (
            @RequestHeader("X-Account-Id") Long accountId,
            @RequestBody CreateUserWordbookFolderRequestForm requestForm) {
        log.info("Received request for new user wordbook folder: {}", requestForm);
        CreateUserWordbookFolderRequest request = requestForm.toCreateFolderRequest(accountId);
        CreateUserWordbookFolderResponse response = userWordbookFolderService.registerWordbookFolder(request);
        return CreateUserWordbookFolderResponseForm.from(response);
    }

    // 암기 상태 변경 : 로그인 사용자 기준, termId로 직접 상태 변경
    @PatchMapping("/me/terms/{termId}/memorization")
    public UpdateMemorizationResponseForm updateMemorizationByTermId(
            @RequestHeader("X-Account-Id") Long accountId,   // ← required=true (기본값)
            @PathVariable Long termId,
            @RequestBody @Valid UpdateMemorizationRequestForm requestForm
    ) {
        log.info("memorization update: accountId={}, termId={}, status={}",
                accountId, termId, requestForm.getStatus());

        UpdateMemorizationRequest request = requestForm.toUpdateMemorizationRequest(accountId, termId);
        UpdateMemorizationResponse response = memorizationService.updateMemorization(request);
        return UpdateMemorizationResponseForm.from(response);
    }

    // 암기 상태 변경 : userTermId 기준
    @PatchMapping("/me/user-terms/{userTermId}/memorization")
    public UpdateMemorizationResponseForm updateMemorizationByUserTermId(
            @RequestHeader("X-Account-Id") Long accountId,   // ← required=true
            @PathVariable Long userTermId,
            @RequestBody @Valid UpdateMemorizationRequestForm requestForm
    ) {
        log.info("memorization update (by userTerm): accountId={}, userTermId={}, status={}",
                accountId, userTermId, requestForm.getStatus());

        UserWordbookTerm uwt = userWordbookTermRepository.findById(userTermId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "선택하신 용어를 찾을 수 없습니다."));

        UpdateMemorizationRequest request =
                requestForm.toUpdateMemorizationRequest(accountId, uwt.getTerm().getId());
        UpdateMemorizationResponse response = memorizationService.updateMemorization(request);
        return UpdateMemorizationResponseForm.from(response);
    }

    // 최근 학습/열람 이벤트 발생 시 ‘최근 본 용어’로 저장하기
    @PostMapping("/terms/{termId}/view")
    public ResponseEntity<RecordTermViewResponseForm> view(
            @RequestHeader("X-Account-Id") Long accountId,
            @PathVariable Long termId,
            @RequestBody @Valid RecordTermViewRequestForm requestForm) {
        RecordTermViewRequest request = requestForm.toRecordTermViewRequest(accountId, termId);
        RecordTermViewResponse response = userRecentTermService.recordTermView(request);
        return ResponseEntity.ok(RecordTermViewResponseForm.from(response));
    }

    // 인증된 사용자가 폴더별로 단어장 조회하기
    @GetMapping("/folders/{folderId}/terms")
    public ListUserWordbookTermResponseForm userTermList(
            @RequestHeader("X-Account-Id") Long accountId,
            @PathVariable Long folderId,
            @ModelAttribute ListUserWordbookTermRequestForm requestForm) {
        log.info("Received request for user term list: {}", requestForm);
        ListUserWordbookTermRequest request = requestForm.toListUserTermRequest(accountId, folderId);
        ListUserWordbookTermResponse response = userWordbookFolderService.list(request);
        return ListUserWordbookTermResponseForm.from(response);
    }




}
