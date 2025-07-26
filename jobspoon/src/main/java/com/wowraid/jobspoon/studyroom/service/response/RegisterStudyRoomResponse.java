package com.wowraid.jobspoon.studyroom.service.response;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegisterStudyRoomResponse {
    private final Long studyRoomId;
    private final String studyTitle;
    private final String description;
    private final String status;
    private final String region;
    private final LocalDateTime createdAt;

    private RegisterStudyRoomResponse(Long studyRoomId, String studyTitle, String description, String status, String region, LocalDateTime createdAt) {
        this.studyRoomId = studyRoomId;
        this.studyTitle = studyTitle;
        this.description = description;
        this.status = status;
        this.region = region;
        this.createdAt = createdAt;
    }

    public static RegisterStudyRoomResponse from(StudyRoom studyRoom) {
        return new RegisterStudyRoomResponse(
                studyRoom.getId(),
                studyRoom.getStudyTitle(),
                studyRoom.getDescription(),
                studyRoom.getStatus(),
                studyRoom.getRegion(),
                studyRoom.getCreatedAt());

    }
}
