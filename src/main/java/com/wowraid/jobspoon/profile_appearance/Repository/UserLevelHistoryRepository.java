package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.UserLevelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLevelHistoryRepository extends JpaRepository<UserLevelHistory, Long> {
    // 특정 유저의 레벨업 이력(최신순)
    List<UserLevelHistory> findByAccountIdOrderByAchievedAtDesc(Long accountId);
}
