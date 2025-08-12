package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.UserDashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDashboardRepository extends JpaRepository<UserDashboard, Long> {
    boolean existsByAccount_Id(Long accountId);
    Optional<UserDashboard> findByAccount_Id(Long accountId);
}
