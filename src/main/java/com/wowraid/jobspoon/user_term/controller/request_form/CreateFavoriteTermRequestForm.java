package com.wowraid.jobspoon.user_term.controller.request_form;

import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFavoriteTermRequestForm {

    @NotNull
    private Long termId;

    public CreateFavoriteTermRequest toCreateFavoriteTermRequest(Long accountId) {
        return new CreateFavoriteTermRequest(accountId, termId);
    }
}
