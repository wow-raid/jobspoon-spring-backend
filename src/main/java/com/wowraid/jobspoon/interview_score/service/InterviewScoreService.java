package com.wowraid.jobspoon.interview_score.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview_score.entity.InterviewScore;

public interface InterviewScoreService {

    InterviewScore createInterviewScore(InterviewResultRequestForm  interviewResultRequestForm);

}
