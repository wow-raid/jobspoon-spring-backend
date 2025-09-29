package com.wowraid.jobspoon.interview.service.response;

import com.wowraid.jobspoon.interview.controller.response_form.InterviewCreateResponseForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import lombok.Getter;

@Getter
public class InterviewCreateResponse {

    private Long interviewId;

    public InterviewCreateResponse(Long interviewId) {
        this.interviewId = interviewId;
    }

    public InterviewCreateResponse() {
    }


}
