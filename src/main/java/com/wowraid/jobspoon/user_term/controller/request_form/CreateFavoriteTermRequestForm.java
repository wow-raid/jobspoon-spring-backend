package com.wowraid.jobspoon.user_term.controller.request_form;

import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateFavoriteTermRequestForm {
    private final Long termId;

    public CreateFavoriteTermRequest toCreateFavoriteTermRequest() {
        return new CreateFavoriteTermRequest(termId);
    }
}
