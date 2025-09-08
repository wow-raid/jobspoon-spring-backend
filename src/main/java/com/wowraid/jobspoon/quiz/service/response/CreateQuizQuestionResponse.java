package com.wowraid.jobspoon.quiz.service.response;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateQuizQuestionResponse {

    private final String message;
    private final Long questionId;
    private final QuestionType questionType;
    private final String questionText;
    private final Integer questionAnswer;

    public static CreateQuizQuestionResponse from(QuizQuestion quizQuestion) {
        String message = "문제가 성공적으로 등록되었습니다.";
        return new CreateQuizQuestionResponse(
                message,
                quizQuestion.getId(),
                quizQuestion.getQuestionType(),
                quizQuestion.getQuestionText(),
                quizQuestion.getQuestionAnswer()
        );
    }
}
