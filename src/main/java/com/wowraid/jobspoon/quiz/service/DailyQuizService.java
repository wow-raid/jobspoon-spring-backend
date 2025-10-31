package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.service.response.BuiltQuizSetResponse;
import com.wowraid.jobspoon.quiz.service.response.InitialsQuestionRead;

import java.time.LocalDate;
import java.util.List;

public interface DailyQuizService {
    BuiltQuizSetResponse resolve(LocalDate date, QuizPartType part, JobRole role);
    List<InitialsQuestionRead> loadInitialsQuestions(List<Long> questionIds);
}
