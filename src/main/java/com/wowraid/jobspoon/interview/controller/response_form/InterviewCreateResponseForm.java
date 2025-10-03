package com.wowraid.jobspoon.interview.controller.response_form;


import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterviewCreateResponseForm {

    private final Long interviewId;
    private final Long interviewQAId;
    private final String interviewQuestion;




    public static InterviewCreateResponseForm of(InterviewCreateResponse interviewCreateResponse) {
        return new InterviewCreateResponseForm(interviewCreateResponse.getInterviewQAId(), interviewCreateResponse.getInterviewId(), interviewCreateResponse.getInterviewQuestion());
    }

}
