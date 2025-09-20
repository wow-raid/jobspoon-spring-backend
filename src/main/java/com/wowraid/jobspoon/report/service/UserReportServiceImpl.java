package com.wowraid.jobspoon.report.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.report.entity.ReportStatus;
import com.wowraid.jobspoon.report.entity.UserReport;
import com.wowraid.jobspoon.report.repository.UserReportRepository;
import com.wowraid.jobspoon.report.service.request.CreateReportRequest;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserReportServiceImpl implements UserReportService {

    private final UserReportRepository userReportRepository;
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
        boolean isDuplicate = userReportRepository.existsByReporterAndReportedUserAndStatus(
                reporter, reportedUser, ReportStatus.PENDING
        );
        if (isDuplicate) {
            throw new IllegalStateException("이미 해당 사용자에 대해 처리 중인 신고가 존재합니다.");
        }

        // 4. 신고 엔티티 생성 및 저장
        UserReport newUserReport = UserReport.create(
                reporter,
                reportedUser,
                studyRoom,
                request.getCategory(),
                request.getDescription()
        );
        userReportRepository.save(newUserReport);
    }
}