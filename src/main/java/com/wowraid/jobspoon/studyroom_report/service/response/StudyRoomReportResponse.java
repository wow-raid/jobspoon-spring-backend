package com.wowraid.jobspoon.studyroom_report.service.response;

import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReport;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportCategory;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class StudyRoomReportResponse {
    private final Long reportId;
    private final String reporterNickname;
    private final String reportedUserNickname;
    private final String studyRoomTitle;
    private final StudyRoomReportCategory category;
    private final String description;
    private final StudyRoomReportStatus status;
    private final LocalDateTime createdAt;

    public StudyRoomReportResponse(StudyRoomReport report) {
        this.reportId = report.getId();
        this.reporterNickname = report.getReporter().getNickname();
        this.reportedUserNickname = report.getReportedUser().getNickname();
        this.studyRoomTitle = report.getStudyRoom().getTitle();
        this.category = report.getCategory();
        this.description = report.getDescription();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt();
    }
}