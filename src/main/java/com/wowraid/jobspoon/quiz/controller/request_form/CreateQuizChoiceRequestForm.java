package com.wowraid.jobspoon.quiz.controller.request_form;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizChoiceRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateQuizChoiceRequestForm {

    private QuestionType questionType;
    private List<ChoiceForm> choices;

    @Getter
    @RequiredArgsConstructor
    public static class ChoiceForm {
        private final String choiceText;
        private final boolean isAnswer;
        private final String explanation;
    }

    public List<CreateQuizChoiceRequest> toCreateQuizChoiceRequest(Long quizQuestionId) {
        return choices.stream()
                .map(choice -> new CreateQuizChoiceRequest(
                        quizQuestionId,
                        choice.getChoiceText(),
                        choice.isAnswer(),
                        choice.getExplanation()
                )).toList();
    }

}
