package com.wowraid.jobspoon.interview.controller.response_form;


import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterviewCreateResponseForm {

    private final Long interviewId;




    public static InterviewCreateResponseForm of(InterviewCreateResponse interviewCreateResponse) {
        return new InterviewCreateResponseForm(interviewCreateResponse.getInterviewId());
    }

}
