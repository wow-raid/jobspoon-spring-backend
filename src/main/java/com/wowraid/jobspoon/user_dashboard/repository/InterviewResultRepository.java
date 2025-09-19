package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
    // 특정 유저의 마지막 인터뷰 결과
    InterviewResult findTopByAccountIdOrderByCreatedAtDesc(Long accountId);

    // 특정 인터뷰의 결과 존재 여부 확인
    boolean existsByInterviewId(Long interviewId);

    // 특정 인터뷰 ID 기반 결과 조회
    List<InterviewResult> findByInterviewId(Long interviewId);

    // 완료 횟수 집계 (누적)
    long countByAccountId(Long accountId);

    // 완료 횟수 집계 (월간)
    long countByAccountIdAndCreatedAtBetween(Long accountId, LocalDateTime start, LocalDateTime end);
}
