package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    // account_id 기준 목록 조회 (전체)
    List<Interview> findByAccountId(Long accountId);

    // account_id 기준 목록 조회 (페이징)
    Page<Interview> findByAccountId(Long accountId, Pageable pageable);

    // 시도 횟수 집계
    long countByAccountId(Long accountId);
}
