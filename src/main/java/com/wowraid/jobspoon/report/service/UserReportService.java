package com.wowraid.jobspoon.report.service;

import com.wowraid.jobspoon.report.service.request.CreateReportRequest;

public interface UserReportService {

    // 신고 생성
    void createReport(CreateReportRequest request, Long reportedId);
}
