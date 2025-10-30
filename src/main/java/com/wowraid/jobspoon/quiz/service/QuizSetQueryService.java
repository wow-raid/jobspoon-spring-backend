package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.service.response.ChoiceQuestionRead;
import com.wowraid.jobspoon.quiz.service.response.InitialsQA;

import java.util.List;
import java.util.Optional;

public interface QuizSetQueryService {
    List<ChoiceQuestionRead> findChoiceQuestionsBySetId(Long setId);
    List<Long> findQuestionIdsBySetId(Long setId);
    Optional<QuizPartType> findPartTypeBySetId(Long setId);
    List<QuizQuestion> findInitialsQuestionsBySetId(Long setId);
}
