package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateStudyRoomResponseForm {
    private final Long studyRoomId;
    private final String studyTitle;
    private final String description;
    private final String status;
    private final String region;
    private final LocalDateTime createdAt;

    private CreateStudyRoomResponseForm(Long studyRoomId, String studyTitle, String description, String status, String region, LocalDateTime createdAt) {
        this.studyRoomId = studyRoomId;
        this.studyTitle = studyTitle;
        this.description = description;
        this.status = status;
        this.region = region;
        this.createdAt = createdAt;
    }

    public static CreateStudyRoomResponseForm from(CreateStudyRoomResponse response){
        return new CreateStudyRoomResponseForm(
                response.getStudyRoomId(),
                response.getStudyTitle(),
                response.getDescription(),
                response.getStatus(),
                response.getRegion(),
                response.getCreatedAt()
        );
    }
}
