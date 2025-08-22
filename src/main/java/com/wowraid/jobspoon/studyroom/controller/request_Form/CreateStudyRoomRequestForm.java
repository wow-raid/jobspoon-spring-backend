package com.wowraid.jobspoon.studyroom.controller.request_Form;

import lombok.Getter;

@Getter
public class CreateStudyRoomRequestForm {
    private String studyTitle;
    private String description;
    private int maxMembers;
    private String status;
    private String region;
    private String chatLink;

}
