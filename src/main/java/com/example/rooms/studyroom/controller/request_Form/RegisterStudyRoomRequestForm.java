package com.example.rooms.studyroom.controller.request_Form;

import lombok.Getter;

@Getter
public class RegisterStudyRoomRequestForm {
    private String studyTitle;
    private String description;
    private int maxMembers;
    private String status;
    private String region;
    private String chatLink;

}
