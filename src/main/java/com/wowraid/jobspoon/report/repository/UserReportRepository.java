package com.wowraid.jobspoon.report.repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.report.entity.ReportStatus;
import com.wowraid.jobspoon.report.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    // 특정 사용자에 대한 처리중인 신고가 있는지 확인 == 중복 신고방지
    boolean existsByReporterAndReportedUserAndStatus(AccountProfile reporter, AccountProfile reportedUser, ReportStatus status);
}