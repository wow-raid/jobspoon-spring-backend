package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizChoice;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizChoiceRequest;
import com.wowraid.jobspoon.quiz.service.response.CreateQuizChoiceListResponse;

import java.util.List;

public interface QuizChoiceService {
    // 용어 기반 문제 등록
    List<QuizChoice> registerQuizChoices(Long quizQuestionId, List<CreateQuizChoiceRequest> requestList);
}
