package com.wowraid.jobspoon.studyroom.controller.request_Form;

import com.wowraid.jobspoon.studyroom.entity.StudyLevel;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomRequestForm {
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final String location;
    private final String studyLevel;
    private final Set<String> recruitingRoles; // 👈 List -> Set
    private final Set<String> skillStack;      // 👈 List -> Set


    public UpdateStudyRoomRequest toServiceRequest() {
        return new UpdateStudyRoomRequest(
                title,
                description,
                maxMembers,
                StudyLocation.valueOf(location.toUpperCase()),
                StudyLevel.valueOf(studyLevel.toUpperCase()),
                recruitingRoles,
                skillStack
        );
    }
}