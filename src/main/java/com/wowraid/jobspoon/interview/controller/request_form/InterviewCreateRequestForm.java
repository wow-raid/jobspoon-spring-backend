package com.wowraid.jobspoon.interview.controller.request_form;


import com.wowraid.jobspoon.interview.controller.request.InterviewQARequest;
import com.wowraid.jobspoon.interview.controller.request.IntervieweeProfileRequest;
import com.wowraid.jobspoon.interview.entity.InterviewType;
import com.wowraid.jobspoon.interviewee_profile.entity.TechStack;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InterviewCreateRequestForm {

    private InterviewType interviewType;
    private String company;       // ex) "당근마켓"
    private String major;         // ex) "전공자"
    private String career;        // ex) "3년 이하"
    private String projectExp;    // ex) "있음"
    private String job;           // ex) "Backend"
    private String projectDescription;  // ex) "job-spoon 프로젝트는 ai 면접...."
    private List<TechStack> techStacks;  // ex) "풀스택, 백엔드, ..."
    private String firstQuestion;
    private String firstAnswer;


    public InterviewQARequest toInterviewQARequest() {
        return new InterviewQARequest(firstQuestion,firstAnswer);
    }

    public IntervieweeProfileRequest toIntervieweeProfileRequest() {
        return new IntervieweeProfileRequest(
                company,major,career,projectExp,job,projectDescription,techStacks
        );
    }




}
