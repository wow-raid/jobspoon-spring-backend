package com.wowraid.jobspoon.studyroom.service.request;

import lombok.Getter;

@Getter
public class RegisterStudyRoomRequest {
    private String studyTitle;
    private String description;
    private int maxMembers;
    private String status;
    private String region;
    private String chatLink;
}
