package com.wowraid.jobspoon.quiz.controller.response_form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SubmitQuizSessionResponseForm {
    private final Long sessionId;
    private final int total;
    private final int correct;
    private final Long elapsedMs;
    private final List<Item> details;

    @Getter
    @RequiredArgsConstructor
    public static class Item {
        private final Long quizQuestionId;
        private final Long selectedChoiceId;
        private final boolean correct;
    }
}
