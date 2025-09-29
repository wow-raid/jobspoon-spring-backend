package com.wowraid.jobspoon.interviewQA.service;

import com.wowraid.jobspoon.interview.controller.request.InterviewQARequest;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewCreateResponseForm;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;

public interface InterviewQAService {

    InterviewQA createInterviewQA(InterviewQARequest interviewQARequest);


}
