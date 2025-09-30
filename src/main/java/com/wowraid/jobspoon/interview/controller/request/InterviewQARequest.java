package com.wowraid.jobspoon.interview.controller.request;

import lombok.Getter;

@Getter
public class InterviewQARequest {

    private String firstQuestion;
    private String firstAnswer;

    public InterviewQARequest(String firstQuestion, String firstAnswer) {
        this.firstQuestion = firstQuestion;
        this.firstAnswer = firstAnswer;
    }

    public InterviewQARequest() {
    }
}
