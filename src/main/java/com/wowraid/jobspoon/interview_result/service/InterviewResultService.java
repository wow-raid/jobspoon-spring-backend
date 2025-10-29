package com.wowraid.jobspoon.interview_result.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview.controller.response_form.InterviewResultResponseForm;
import com.wowraid.jobspoon.interview_result.entity.InterviewResult;
import com.wowraid.jobspoon.interview_result.entity.InterviewResultDetail;

import java.util.List;

public interface InterviewResultService {

    InterviewResult createInterviewResult(InterviewResultRequestForm interviewResultRequestForm);
    InterviewResultResponseForm getInterviewResult(Long interviewId);
    boolean checkInterviewOwnership(Long accountId, Long interviewId);
    List<InterviewResultResponseForm.Qa> convertInterviewResultDetailToResponseFormList(List<InterviewResultDetail> interviewResultDetail);


}
