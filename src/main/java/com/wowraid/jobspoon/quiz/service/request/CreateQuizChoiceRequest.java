package com.wowraid.jobspoon.quiz.service.request;

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateQuizChoiceRequest {
    private final Long quizQuestionId;
    private final String choiceText;
    private final boolean isAnswer;
    private final String explanation;

    public QuizChoice toQuizChoice(QuizQuestion quizQuestion) {
        return new QuizChoice(quizQuestion, choiceText, isAnswer, explanation);
    }

}
