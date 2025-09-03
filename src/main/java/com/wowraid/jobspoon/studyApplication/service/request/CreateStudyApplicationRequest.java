package com.wowraid.jobspoon.studyApplication.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateStudyApplicationRequest {
    private final Long studyRoomId;
    private final Long applicantId;
    private final String message;
}
