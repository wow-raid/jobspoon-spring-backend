package com.wowraid.jobspoon.quiz.controller.request_form;

import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.term.entity.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateQuizSetByCategoryRequestForm {
    private final String title;
    private final String categoryId;
    private final boolean isRandom;

    public CreateQuizSetByCategoryRequest toCategoryBasedRequest() {
        return new CreateQuizSetByCategoryRequest(title, categoryId, isRandom);
    }

}
