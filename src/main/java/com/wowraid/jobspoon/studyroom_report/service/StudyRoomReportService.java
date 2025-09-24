package com.wowraid.jobspoon.studyroom_report.service;

import com.wowraid.jobspoon.studyroom_report.service.request.CreateReportRequest;
import com.wowraid.jobspoon.studyroom_report.service.response.StudyRoomReportResponse;

import java.util.List;

public interface StudyRoomReportService {

    // 신고 생성
    void createReport(CreateReportRequest request, Long reportedId);

    // 신고 조회
    List<StudyRoomReportResponse> findAllReports();
}
