package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.request_form.SubmitQuizSessionRequestForm;
import com.wowraid.jobspoon.quiz.controller.response_form.SubmitQuizSessionResponseForm;
import com.wowraid.jobspoon.quiz.entity.SessionAnswer;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;

import java.util.List;

public interface UserQuizAnswerService {
    StartUserQuizSessionResponse startFromQuizSet(Long accountId, Long quizSetId, List<Long> questionIds, SeedMode seedMode, Long fixedSeed);
    StartUserQuizSessionResponse startRetryWrongOnly(Long parentSessionId, Long accountId);
    SubmitQuizSessionResponseForm submitSession(Long sessionId, Long accountId, SubmitQuizSessionRequestForm form);
    void saveWrongNotes(List<SessionAnswer> answers, Long accountId);
}