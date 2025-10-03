package com.wowraid.jobspoon.interview.controller.request;

import com.wowraid.jobspoon.interview.entity.Interview;
import lombok.Getter;

@Getter
public class InterviewQARequest {

    private String firstQuestion;
    private String firstAnswer;
    private Interview interview;

    public InterviewQARequest(Interview interview,String firstQuestion, String firstAnswer) {
        this.interview = interview;
        this.firstQuestion = firstQuestion;
        this.firstAnswer = firstAnswer;
    }

    public InterviewQARequest() {
    }
}
