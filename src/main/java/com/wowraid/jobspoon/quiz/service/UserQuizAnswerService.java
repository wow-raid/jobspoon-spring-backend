package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.request_form.SubmitAnswerRequestForm;
import com.wowraid.jobspoon.quiz.entity.UserQuizAnswer;
import com.wowraid.jobspoon.quiz.entity.UserWrongNote;

import java.util.List;

public interface UserQuizAnswerService {
    List<UserQuizAnswer> registerQuizResult(Long accountId, List<SubmitAnswerRequestForm> requestList);
    void saveWrongNotes(List<UserQuizAnswer> answers, Long accountId);

}
