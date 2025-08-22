package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.request_form.SubmitAnswerRequestForm;
import com.wowraid.jobspoon.quiz.entity.SessionAnswer;

import java.util.List;

public interface UserQuizAnswerService {
    List<SessionAnswer> registerQuizResult(Long accountId, List<SubmitAnswerRequestForm> requestList);
    void saveWrongNotes(List<SessionAnswer> answers, Long accountId);

}
