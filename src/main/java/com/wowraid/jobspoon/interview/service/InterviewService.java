package com.wowraid.jobspoon.interview.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;

public interface InterviewService {

    InterviewCreateResponse createInterview(InterviewCreateRequestForm interviewCreateRequestForm, Long accountId);


}
