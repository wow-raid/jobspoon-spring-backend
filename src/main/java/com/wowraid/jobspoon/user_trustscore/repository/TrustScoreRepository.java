package com.wowraid.jobspoon.user_trustscore.repository;

import com.wowraid.jobspoon.user_trustscore.entity.TrustScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrustScoreRepository extends JpaRepository<TrustScore, Long> {

    // accountId로 TrustScore 조회
    Optional<TrustScore> findByAccountId(Long accountId);

    // 탈퇴 시 등 삭제 용도
    void deleteAllByAccountId(Long accountId);
}
