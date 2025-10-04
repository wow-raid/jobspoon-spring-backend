package com.wowraid.jobspoon.interview.service.strategy.sequence_strategy;

import com.wowraid.jobspoon.interview.service.request.InterviewSequenceRequest;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;

public interface InterviewSequenceStrategy {
    InterviewProgressResponse getQuestionByCompany(InterviewSequenceRequest interviewSequenceRequest, String userToken);
}
