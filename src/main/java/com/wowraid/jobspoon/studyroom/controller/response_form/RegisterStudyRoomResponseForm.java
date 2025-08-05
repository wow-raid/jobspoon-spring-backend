package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegisterStudyRoomResponseForm {
    private final Long studyRoomId;
    private final String studyTitle;
    private final String description;
    private final String status;
    private final String region;
    private final LocalDateTime createdAt;

    private RegisterStudyRoomResponseForm(Long studyRoomId, String studyTitle, String description, String status, String region, LocalDateTime createdAt) {
        this.studyRoomId = studyRoomId;
        this.studyTitle = studyTitle;
        this.description = description;
        this.status = status;
        this.region = region;
        this.createdAt = createdAt;
    }

    public static RegisterStudyRoomResponseForm from(RegisterStudyRoomResponse response){
        return new RegisterStudyRoomResponseForm(
                response.getStudyRoomId(),
                response.getStudyTitle(),
                response.getDescription(),
                response.getStatus(),
                response.getRegion(),
                response.getCreatedAt()
        );
    }
}
