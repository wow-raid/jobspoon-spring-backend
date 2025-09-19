package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.entity.Interview;

import java.util.List;

public interface InterviewService {
    Interview createInterview(Long accountId, String topic, int experienceLevel,
                              int projectExperience, int academicBackground,
                              String techStack, String companyName);

    List<Interview> listInterview(Long accountId, int page, int pageSize);

    void deleteInterview(Long accountId, Long interviewId);
}
