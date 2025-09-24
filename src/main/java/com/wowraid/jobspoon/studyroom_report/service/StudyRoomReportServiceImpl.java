package com.wowraid.jobspoon.studyroom_report.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReport;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportStatus;
import com.wowraid.jobspoon.studyroom_report.repository.StudyRoomReportRepository;
import com.wowraid.jobspoon.studyroom_report.service.request.CreateReportRequest;
import com.wowraid.jobspoon.studyroom_report.service.response.StudyRoomReportResponse;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyRoomReportServiceImpl implements StudyRoomReportService {

    private final StudyRoomReportRepository studyRoomReportRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final StudyRoomRepository studyRoomRepository;

    @Override
    public void createReport(CreateReportRequest request, Long reporterId) {
        // 1. 신고자, 신고 대상자, 스터디룸 정보 조회
        AccountProfile reporter = accountProfileRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));
        AccountProfile reportedUser = accountProfileRepository.findById(request.getReportedUserId())
                .orElseThrow(() -> new IllegalArgumentException("신고 대상자를 찾을 수 없습니다."));
        StudyRoom studyRoom = studyRoomRepository.findById(request.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("스터디룸을 찾을 수 없습니다."));

        // 2. 자기 자신을 신고하는 경우 방지
        if (reporter.getId().equals(reportedUser.getId())) {
            throw new IllegalStateException("자기 자신을 신고할 수 없습니다.");
        }

        // 3. 중복 신고 방지 로직
        boolean isDuplicate = studyRoomReportRepository.existsByReporterAndReportedUserAndStatus(
                reporter, reportedUser, StudyRoomReportStatus.PENDING
        );
        if (isDuplicate) {
            throw new IllegalStateException("이미 해당 사용자에 대해 처리 중인 신고가 존재합니다.");
        }

        // 4. 신고 엔티티 생성 및 저장
        StudyRoomReport newUserReport = StudyRoomReport.create(
                reporter,
                reportedUser,
                studyRoom,
                request.getCategory(),
                request.getDescription()
        );
        studyRoomReportRepository.save(newUserReport);
    }

    // 신고목록 조회 구현
    @Override
    @Transactional(readOnly = true)
    public List<StudyRoomReportResponse> findAllReports() {
        return studyRoomReportRepository.findAll().stream()
                .map(StudyRoomReportResponse::new)
                .collect(Collectors.toList());
    }
}