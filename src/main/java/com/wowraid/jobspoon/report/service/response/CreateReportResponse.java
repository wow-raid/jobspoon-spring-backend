package com.wowraid.jobspoon.report.service.response;

import com.wowraid.jobspoon.report.entity.Report;
import com.wowraid.jobspoon.report.entity.ReportCategory;
import com.wowraid.jobspoon.report.entity.ReportStatus;
import com.wowraid.jobspoon.report.entity.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateReportResponse {
    private final Long id;
    private final String reporterNickname;
    private final String reportedUserNickname;
    private final ReportType reportType;
    private final Long sourceId;
    private final ReportCategory category;
    private final String description;
    private final ReportStatus status;
    private final LocalDateTime createdAt;

    private CreateReportResponse(Report report) {
        this.id = report.getId();
        this.reporterNickname = report.getReporter().getNickname();
        this.reportedUserNickname = report.getReportedUser().getNickname();
        this.reportType = report.getReportType(); //  studyRoom.getTitle() 대신 reportType을 가져옴
        this.sourceId = report.getSourceId();     //  studyRoom.getTitle() 대신 sourceId를 가져옴
        this.category = report.getCategory();
        this.description = report.getDescription();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt();
    }

    public static CreateReportResponse from(Report report) {
        return new CreateReportResponse(report);
    }
}