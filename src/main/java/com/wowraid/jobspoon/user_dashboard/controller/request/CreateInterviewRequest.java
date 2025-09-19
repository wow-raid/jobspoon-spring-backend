package com.wowraid.jobspoon.user_dashboard.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateInterviewRequest {
    private String topic;
    private int experienceLevel;
    private int projectExperience;
    private int academicBackground;
    private String techStack; // JSON 문자열 형태
    private String companyName;
}
