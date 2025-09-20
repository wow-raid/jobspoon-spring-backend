package com.wowraid.jobspoon.studyroom.service.response;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.entity.StudyStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MyStudyResponse {
    private final Long id;
    private final String title;
    private final StudyStatus status;
    private final Integer currentMembers;
    private final Integer maxMembers;
    private final String location;
    private final List<String> skillStack;

    public static MyStudyResponse from(StudyRoom studyRoom) {
        return new MyStudyResponse(
                studyRoom.getId(),
                studyRoom.getTitle(),
                studyRoom.getStatus(),
                studyRoom.getStudyMembers().size(),
                studyRoom.getMaxMembers(),
                studyRoom.getLocation().name(),
                new ArrayList<>(studyRoom.getSkillStack())
        );
    }
}