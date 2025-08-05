package com.wowraid.jobspoon.quiz.controller.response_form;

import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateQuizSetByCategoryResponseForm {
    private final String message;
    private final Long quizSetId;
    private final String title;
    private final boolean isRandom;

    public static CreateQuizSetByCategoryResponseForm from(CreateQuizSetByCategoryResponse response) {
        return new CreateQuizSetByCategoryResponseForm(
                response.getMessage(),
                response.getQuizSetId(),
                response.getTitle(),
                response.isRandom()
        );
    }
}
