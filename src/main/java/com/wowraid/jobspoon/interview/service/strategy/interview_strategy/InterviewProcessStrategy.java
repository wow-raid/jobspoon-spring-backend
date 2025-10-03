package com.wowraid.jobspoon.interview.service.strategy.interview_strategy;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewProgressRequestForm;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;

public interface InterviewProcessStrategy {
    InterviewProgressResponse process(InterviewProgressRequestForm interviewProgressRequestForm, String userToken);
}
