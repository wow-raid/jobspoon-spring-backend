package com.wowraid.jobspoon.studyroom_report.service;

import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportStatus;
import com.wowraid.jobspoon.studyroom_report.service.request.CreateStudyRoomReportRequest;
import com.wowraid.jobspoon.studyroom_report.service.response.StudyRoomReportResponse;

import java.util.List;

public interface StudyRoomReportService {

    // 신고 생성
    void createReport(CreateStudyRoomReportRequest request, Long reportedId);

    // 신고 조회
    List<StudyRoomReportResponse> findAllReports();

    // 신고 상태 변경
    void updateReportStatus(Long reportId, StudyRoomReportStatus status);
}
