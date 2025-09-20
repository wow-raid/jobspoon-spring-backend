package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.TrustScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrustScoreRepository extends JpaRepository<TrustScore, Long> {
    // 특정 계정의 가장 최근 신뢰점수
    Optional<TrustScore> findTopByAccountIdOrderByCalculatedAtDesc(Long accountId);

    // 특정 계정의 해당 월 신뢰점수 (필요하면 LocalDate 기준 추가)
    Optional<TrustScore> findByAccountIdAndCalculatedAtBetween(
            Long accountId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );

    void deleteAllByAccountId(Long accountId);
}
