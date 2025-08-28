package com.wowraid.jobspoon.studyroom.service.request;

import com.wowraid.jobspoon.studyroom.entity.StudyStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomStatusRequest {
    private final StudyStatus status;
}