package com.wowraid.jobspoon.interview.service.response;

import com.wowraid.jobspoon.interview.controller.response_form.InterviewCreateResponseForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import lombok.Getter;

@Getter
public class InterviewCreateResponse {

    private Long interviewId;
    private Long interviewQAId;
    private String interviewQuestion;

    public InterviewCreateResponse(String interviewQuestion, Long interviewQAId, Long interviewId) {
        this.interviewQuestion = interviewQuestion;
        this.interviewQAId = interviewQAId;
        this.interviewId = interviewId;
    }

    public InterviewCreateResponse() {
    }


}
