package com.wowraid.jobspoon.quiz.service.request;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateQuizQuestionRequest {

    private final Long termId;
    private final Long categoryId;
    private final QuestionType questionType;
    private final String questionText;
    private final Integer questionAnswer;

    public QuizQuestion toQuizQuestion(Term term, Category category) {
        return new QuizQuestion(term, category, questionType, questionText, questionAnswer);
    }

}
