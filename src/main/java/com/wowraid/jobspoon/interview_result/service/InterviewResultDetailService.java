package com.wowraid.jobspoon.interview_result.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview_result.entity.InterviewResultDetail;

import java.util.List;
import java.util.Optional;

public interface InterviewResultDetailService {

    List<InterviewResultDetail> createInterviewResultDetail(InterviewResultRequestForm interviewResultRequestForm, Long interviewResultId);
    List<InterviewResultDetail> findAllByInterviewResultId(Long interviewResultId);

}
