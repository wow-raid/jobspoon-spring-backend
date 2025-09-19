package com.wowraid.jobspoon.user_dashboard.controller.response;

import com.wowraid.jobspoon.user_dashboard.entity.Interview;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewResponse {
    private Long id;
    private String topic;
    private int experienceLevel;
    private int projectExperience;
    private int academicBackground;
    private String techStack;
    private String companyName;
    private LocalDateTime createdAt;

    public static InterviewResponse fromEntity(Interview entity) {
        return InterviewResponse.builder()
                .id(entity.getId())
                .topic(entity.getTopic())
                .experienceLevel(entity.getExperienceLevel())
                .projectExperience(entity.getProjectExperience())
                .academicBackground(entity.getAcademicBackground())
                .techStack(entity.getTechStack())
                .companyName(entity.getCompanyName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
