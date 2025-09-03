package com.wowraid.jobspoon.studyApplication.controller.request_form;

import com.wowraid.jobspoon.studyApplication.service.request.CreateStudyApplicationRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateStudyApplicationRequestForm {
    private final Long studyRoomId;
    private final String message;

    public CreateStudyApplicationRequest toServiceRequest(Long applicantId) {
        return new CreateStudyApplicationRequest(studyRoomId, applicantId, message);
    }
}
