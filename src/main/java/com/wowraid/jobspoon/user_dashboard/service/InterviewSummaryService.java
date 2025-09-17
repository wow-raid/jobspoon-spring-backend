package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.InterviewParticipationResponse;

public interface InterviewSummaryService {
    InterviewParticipationResponse getParticipationStatus(Long accountId);
}
