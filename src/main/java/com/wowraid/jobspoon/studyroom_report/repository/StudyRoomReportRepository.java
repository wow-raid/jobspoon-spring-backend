package com.wowraid.jobspoon.studyroom_report.repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReport;
import com.wowraid.jobspoon.studyroom_report.entity.StudyRoomReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRoomReportRepository extends JpaRepository<StudyRoomReport, Long> {
    // 특정 사용자에 대한 처리중인 신고가 있는지 확인 == 중복 신고방지
    boolean existsByReporterAndReportedUserAndStatus(AccountProfile reporter, AccountProfile reportedUser, StudyRoomReportStatus status);
}