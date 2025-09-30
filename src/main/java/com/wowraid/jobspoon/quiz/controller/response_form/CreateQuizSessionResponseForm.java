package com.wowraid.jobspoon.quiz.controller.response_form;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateQuizSessionResponseForm {
    private final Long sessionId;
    private final List<Item> items;

    @Getter
    @RequiredArgsConstructor
    public static class Item {
        private final Long questionId;
        private final QuestionType questionType;
        private final String questionText;
        private final List<Option> options;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Option {
        private final Long choiceId;
        private final String text;
    }

    public static CreateQuizSessionResponseForm from(StartUserQuizSessionResponse s) {
        return new CreateQuizSessionResponseForm(
                s.getSessionId(),
                s.getItems().stream()
                        .map(it -> new Item(
                                it.getQuestionId(),
                                it.getQuestionType(),
                                it.getQuestionText(),
                                it.getOptions().stream()
                                        .map(op -> new Option(op.getChoiceId(), op.getText()))
                                        .toList()
                        ))
                        .toList()
        );
    }
}
