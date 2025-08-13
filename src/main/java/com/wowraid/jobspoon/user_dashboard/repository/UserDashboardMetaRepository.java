package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityResponse;
import com.wowraid.jobspoon.user_dashboard.entity.UserDashboardMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDashboardMetaRepository extends JpaRepository<UserDashboardMeta, Long> {
    Optional<UserDashboardMeta> findByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
}