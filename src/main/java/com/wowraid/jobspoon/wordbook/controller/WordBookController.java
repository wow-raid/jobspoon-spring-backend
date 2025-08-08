package com.wowraid.jobspoon.wordbook.controller;

import com.wowraid.jobspoon.wordbook.controller.request_form.CreateFavoriteTermRequestForm;
import com.wowraid.jobspoon.wordbook.controller.response_form.CreateFavoriteTermResponseForm;
import com.wowraid.jobspoon.wordbook.service.WordBookService;
import com.wowraid.jobspoon.wordbook.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.wordbook.service.response.CreateFavoriteTermResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class WordBookController {

    private final WordBookService wordBookService;

    @PostMapping
    public CreateFavoriteTermResponseForm responseForm (@RequestBody CreateFavoriteTermRequestForm requestForm) {
        log.info("Received request for new favorite term: {}", requestForm);
        CreateFavoriteTermRequest request = requestForm.toCreateFavoriteTermRequest();
        CreateFavoriteTermResponse response = wordBookService.registerFavoriteTerm(request);
        return CreateFavoriteTermResponseForm.from(response);
    }

    @DeleteMapping("/{favoriteTermId}")
    public ResponseEntity<?> deleteFavoriteTerm(@PathVariable Long favoriteTermId) {
        log.info("Received request for delete favorite term: {}", favoriteTermId);
        return wordBookService.deleteFavoriteTerm(favoriteTermId);
    }
}
