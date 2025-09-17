package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.InterviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {

    // 누적 시도 횟수
    long countByAccountId(Long accountId);

    // 특정 기간 시도 횟수
    long countByAccountIdAndCreatedAtBetween(
            Long accountId, LocalDateTime start, LocalDateTime end);
}
