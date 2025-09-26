package com.wowraid.jobspoon.studyroom_report.service.request;

import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomReportStatusRequest {
    private final StudyRoomReportStatus status;
}
