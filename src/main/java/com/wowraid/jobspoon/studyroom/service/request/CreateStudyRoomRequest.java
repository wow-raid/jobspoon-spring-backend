package com.wowraid.jobspoon.studyroom.service.request;

import com.wowraid.jobspoon.studyroom.entity.StudyLevel;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateStudyRoomRequest {
     private final Long hostId;       // Account와 연동 후 주석 삭제해야함
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final StudyLocation location;
    private final StudyLevel studyLevel;
    private final List<String> recruitingRoles;
    private final List<String> skillStack;
}