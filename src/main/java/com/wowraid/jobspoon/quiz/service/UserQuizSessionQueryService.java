package com.wowraid.jobspoon.quiz.service;

import com.wowraid.jobspoon.quiz.controller.response_form.SessionItemsPageResponseForm;
import com.wowraid.jobspoon.quiz.controller.response_form.SessionSummaryResponseForm;

public interface UserQuizSessionQueryService {
    SessionSummaryResponseForm getSummary(Long sessionId, Long accountId);
    SessionItemsPageResponseForm getSessionItems(Long sessionId, Long accountId, int offset, int limit);
}
