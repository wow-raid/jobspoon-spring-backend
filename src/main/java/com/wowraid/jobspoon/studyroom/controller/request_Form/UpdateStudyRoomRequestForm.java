package com.wowraid.jobspoon.studyroom.controller.request_Form;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomRequestForm {
    private String studyTitle;
    private String description;

    @Min(value = 2, message = "참여인원은 최소 2명 이상으로 설정해야 합니다.")
    private int maxMembers;
    private String status;
    private String region;
    private String chatLink;
}
