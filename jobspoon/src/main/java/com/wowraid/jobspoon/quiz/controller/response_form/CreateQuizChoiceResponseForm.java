package com.wowraid.jobspoon.quiz.controller.response_form;

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizChoiceResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateQuizChoiceResponseForm {
    private final Long quizChoiceId;
    private final String choiceText;
    private final boolean isAnswer;
    private final String explanation;

    public static CreateQuizChoiceResponseForm from(QuizChoice quizChoice) {
        return new CreateQuizChoiceResponseForm(
                quizChoice.getId(),
                quizChoice.getChoiceText(),
                quizChoice.isAnswer(),
                quizChoice.getExplanation()
        );
    }
}
