package com.wowraid.jobspoon.studyroom_report.service.request;

import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateReportRequest {
    private final Long reportedUserId;
    private final Long studyRoomId;
    private final StudyRoomReportCategory category;
    private final String description;
}
