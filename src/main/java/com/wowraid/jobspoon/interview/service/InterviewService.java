package com.wowraid.jobspoon.interview.service;

import com.wowraid.jobspoon.interview.controller.request.InterviewEndRequest;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewEndRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewProgressRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.entity.InterviewType;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interview.service.response.InterviewResultListResponse;
import com.wowraid.jobspoon.interview.service.response.InterviewResultResponse;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;

import java.util.List;
import java.util.Optional;

public interface InterviewService {

    InterviewCreateResponse createInterview(InterviewCreateRequestForm interviewCreateRequestForm, Long accountId, String userToken);
    InterviewProgressResponse execute(InterviewType type, InterviewProgressRequestForm form, String userToken);
    void endInterview(InterviewEndRequestForm interviewEndRequestForm, String userToken);
    InterviewEndRequest createEndInterviewRequestEndInterviewRequest(InterviewEndRequestForm interviewEndRequestForm, String userToken);
    Optional<Interview> findById(Long id);
    InterviewResultResponse interviewResult(InterviewResultRequestForm interviewResultRequestForm);
    List<InterviewResultListResponse> getInterviewResultListByAccountId(Long accountId);





}
