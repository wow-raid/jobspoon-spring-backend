package com.wowraid.jobspoon.interview_result.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview_result.entity.InterviewResult;

public interface InterviewResultService {

    InterviewResult createInterviewResult(InterviewResultRequestForm interviewResultRequestForm);

}
