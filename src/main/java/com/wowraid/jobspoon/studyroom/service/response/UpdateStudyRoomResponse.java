package com.wowraid.jobspoon.studyroom.service.response;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final String status;
    private final String location;
    private final String studyLevel;
    private final List<String> recruitingRoles;
    private final List<String> skillStack;
    private final LocalDateTime createdAt;

    public static UpdateStudyRoomResponse from(StudyRoom studyRoom) {
        return new UpdateStudyRoomResponse(
                studyRoom.getId(),
                studyRoom.getTitle(),
                studyRoom.getDescription(),
                studyRoom.getMaxMembers(),
                studyRoom.getStatus().name(),
                studyRoom.getLocation().name(),
                studyRoom.getStudyLevel().name(),
                studyRoom.getRecruitingRoles(),
                studyRoom.getSkillStack(),
                studyRoom.getCreatedAt()
        );
    }
}