package com.wowraid.jobspoon.wordbook.controller.response_form;

import com.wowraid.jobspoon.wordbook.service.response.CreateFavoriteTermResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateFavoriteTermResponseForm {
    private final String message;
    private final Long favoriteTermId;
    private final String favoriteTermName;

    public static CreateFavoriteTermResponseForm from(CreateFavoriteTermResponse response) {
        return new CreateFavoriteTermResponseForm(response.getMessage(), response.getFavoriteTermId(), response.getFavoriteTermName());
    }

}
