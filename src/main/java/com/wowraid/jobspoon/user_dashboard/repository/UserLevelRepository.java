package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    // 계정별 유저 레벨 가져오기
    Optional<UserLevel> findByAccountId(Long accountId);

    // 계정 존재 여부 확인
    boolean existsByAccountId(Long accountId);
}
