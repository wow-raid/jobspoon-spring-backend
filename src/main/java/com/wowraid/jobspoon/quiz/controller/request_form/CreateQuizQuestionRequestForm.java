package com.wowraid.jobspoon.quiz.controller.request_form;


import com.wowraid.jobspoon.quiz.entity.QuestionType;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizQuestionRequest;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class CreateQuizQuestionRequestForm {

    private final String categoryId;
    private final QuestionType questionType;
    private final String questionText;
    private final Integer questionAnswer;

    public CreateQuizQuestionRequest toCreateQuizQuestionRequest(Long termId) {
        return new CreateQuizQuestionRequest(termId, categoryId, questionType, questionText, questionAnswer);
    }

}
