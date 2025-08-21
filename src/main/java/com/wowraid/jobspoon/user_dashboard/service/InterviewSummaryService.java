package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.InterviewCompletionResponse;

public interface InterviewSummaryService {
    InterviewCompletionResponse getCompletionStatus(Long accountId);
}
