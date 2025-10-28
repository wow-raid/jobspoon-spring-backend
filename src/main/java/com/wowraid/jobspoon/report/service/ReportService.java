package com.wowraid.jobspoon.report.service;

import com.wowraid.jobspoon.report.entity.ReportStatus;
import com.wowraid.jobspoon.report.service.request.CreateReportRequest;
import com.wowraid.jobspoon.report.service.response.CreateReportResponse;
import com.wowraid.jobspoon.report.service.response.UploadUrlResponse;

import java.util.List;

public interface ReportService {

    // 신고 생성
    void createReport(CreateReportRequest request, Long reportedId);

    // 신고 조회
    List<CreateReportResponse> findAllReports();

    // 신고 상태 변경
    void updateReportStatus(Long reportId, ReportStatus status);

    UploadUrlResponse generateUploadUrl(Long repoterId, String filename);

    List<CreateReportResponse> findReportsByReporter(Long reporterId);
}
