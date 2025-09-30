package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.request_form.SubmitAnswerRequestForm;
import com.wowraid.jobspoon.quiz.controller.request_form.SubmitQuizSessionRequestForm;
import com.wowraid.jobspoon.quiz.controller.response_form.SubmitQuizSessionResponseForm;
import com.wowraid.jobspoon.quiz.entity.SessionAnswer;
import com.wowraid.jobspoon.quiz.service.response.StartUserQuizSessionResponse;

import java.util.List;

public interface UserQuizAnswerService {
    StartUserQuizSessionResponse startFromQuizSet(Long accountId, Long quizSetId, List<Long> questionIds);
    SubmitQuizSessionResponseForm submitSession(Long sessionId, Long accountId, SubmitQuizSessionRequestForm form);
    void saveWrongNotes(List<SessionAnswer> answers, Long accountId);
}