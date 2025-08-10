package com.wowraid.jobspoon.user_term.controller;

import com.wowraid.jobspoon.user_term.controller.request_form.CreateFavoriteTermRequestForm;
import com.wowraid.jobspoon.user_term.controller.request_form.CreateUserWordbookFolderRequestForm;
import com.wowraid.jobspoon.user_term.controller.response_form.CreateFavoriteTermResponseForm;
import com.wowraid.jobspoon.user_term.controller.response_form.CreateUserWordbookFolderResponseForm;
import com.wowraid.jobspoon.user_term.service.FavoriteTermService;
import com.wowraid.jobspoon.user_term.service.UserWordbookFolderService;
import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateFavoriteTermResponse;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-terms")
public class UserTermController {

    private final FavoriteTermService favoriteTermService;
    private final UserWordbookFolderService userWordbookFolderService;

    @PostMapping("/favorites")
    public CreateFavoriteTermResponseForm responseForm (@RequestBody CreateFavoriteTermRequestForm requestForm) {
        log.info("Received request for new favorite term: {}", requestForm);
        CreateFavoriteTermRequest request = requestForm.toCreateFavoriteTermRequest();
        CreateFavoriteTermResponse response = favoriteTermService.registerFavoriteTerm(request);
        return CreateFavoriteTermResponseForm.from(response);
    }

    @DeleteMapping("/favorites/{favoriteTermId}")
    public ResponseEntity<?> deleteFavoriteTerm(@PathVariable Long favoriteTermId) {
        log.info("Received request for delete favorite term: {}", favoriteTermId);
        return favoriteTermService.deleteFavoriteTerm(favoriteTermId);
    }

    @PostMapping("/folders")
    public CreateUserWordbookFolderResponseForm responseForm (@RequestBody CreateUserWordbookFolderRequestForm requestForm) {
        log.info("Received request for new user wordbook folder: {}", requestForm);
        CreateUserWordbookFolderRequest request = requestForm.toCreateFolderRequest();
        CreateUserWordbookFolderResponse response = userWordbookFolderService.registerWordbookFolder(request);
        return CreateUserWordbookFolderResponseForm.from(response);
    }
}
