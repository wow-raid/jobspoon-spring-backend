package com.wowraid.jobspoon.interviewee_profile.service;

import com.wowraid.jobspoon.interview.controller.request.IntervieweeProfileRequest;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;

import java.util.Optional;

public interface IntervieweeProfileService {

    IntervieweeProfile createIntervieweeProfile(IntervieweeProfileRequest intervieweeProfileRequest);
    Optional<IntervieweeProfile> findById(Long id);


}
