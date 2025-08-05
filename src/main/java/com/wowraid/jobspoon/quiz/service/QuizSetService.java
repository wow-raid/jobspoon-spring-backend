package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizSetByCategoryResponse;

public interface QuizSetService {
    // 카테고리 기반 퀴즈 자동 생성
    CreateQuizSetByCategoryResponse registerQuizSetByCategory(CreateQuizSetByCategoryRequest request);
}
