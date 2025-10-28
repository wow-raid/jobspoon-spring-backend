package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import com.wowraid.jobspoon.quiz.service.response.BuiltQuizSetResponse;

import java.time.LocalDate;

public interface DailyQuizService {
    BuiltQuizSetResponse resolve(LocalDate date, QuizPartType part, JobRole role);
}
