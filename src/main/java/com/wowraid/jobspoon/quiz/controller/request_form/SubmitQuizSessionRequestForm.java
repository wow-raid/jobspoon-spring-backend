package com.wowraid.jobspoon.quiz.controller.request_form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SubmitQuizSessionRequestForm {
    @NotEmpty
    private final List<AnswerForm> answers;
    private final Long elaspedMs;

    @Getter
    @RequiredArgsConstructor
    public static class AnswerForm {
        private final Long quizQuestionId;
        private final Long selectredChoiceId;
    }
}
