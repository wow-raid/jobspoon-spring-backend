package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.service.response.ChoiceQuestionRead;

import java.util.List;

public interface QuizSetQueryService {
    List<ChoiceQuestionRead> findChoiceQuestionsBySetId(Long setId);
}
