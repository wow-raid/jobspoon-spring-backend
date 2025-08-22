package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.InterviewStatus;
import com.wowraid.jobspoon.user_dashboard.entity.InterviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InterviewSummaryRepository extends JpaRepository<InterviewSummary, Long> {
    long countByAccountIdAndStatus(Long accountId, InterviewStatus status);
    long countByAccountIdAndStatusAndCreatedAtBetween(
            Long accountId, InterviewStatus status, LocalDateTime start, LocalDateTime end);

}
