package com.wowraid.jobspoon.user_term.controller;

import com.wowraid.jobspoon.user_term.controller.request_form.CreateFavoriteTermRequestForm;
import com.wowraid.jobspoon.user_term.controller.request_form.CreateUserWordbookFolderRequestForm;
import com.wowraid.jobspoon.user_term.controller.request_form.UpdateMemorizationRequestForm;
import com.wowraid.jobspoon.user_term.controller.response_form.CreateFavoriteTermResponseForm;
import com.wowraid.jobspoon.user_term.controller.response_form.CreateUserWordbookFolderResponseForm;
import com.wowraid.jobspoon.user_term.controller.response_form.UpdateMemorizationResponseForm;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.FavoriteTermService;
import com.wowraid.jobspoon.user_term.service.MemorizationService;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderService;
import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.request.UpdateMemorizationRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateFavoriteTermResponse;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import com.wowraid.jobspoon.user_term.service.response.UpdateMemorizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

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
    public CreateUserWordbookFolderResponseForm responseForm (@RequestBody CreateUserWordbookFolderRequestForm requestForm) {
        log.info("Received request for new user wordbook folder: {}", requestForm);
        CreateUserWordbookFolderRequest request = requestForm.toCreateFolderRequest();
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

}
