package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.service.request.CreateQuizQuestionRequest;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizQuestionResponse;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;

public interface QuizService {
    // 용어 기반 문제 등록
    CreateQuizQuestionResponse registerQuizQuestion(CreateQuizQuestionRequest request);

    // 카테고리 기반 퀴즈 자동 생성
    CreateQuizSetByCategoryResponse registerQuizSetByCategory(CreateQuizSetByCategoryRequest request);
}
