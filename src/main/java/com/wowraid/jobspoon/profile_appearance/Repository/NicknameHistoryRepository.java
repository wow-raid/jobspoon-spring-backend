package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.NicknameHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NicknameHistoryRepository extends JpaRepository<NicknameHistory, Long> {
    long countByAccountIdAndChangedAtAfter(Long accountId, LocalDateTime since);
    void deleteByAccountId(Long accountId);
}
