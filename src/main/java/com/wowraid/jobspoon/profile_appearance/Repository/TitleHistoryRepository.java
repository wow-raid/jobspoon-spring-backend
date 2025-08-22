package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleHistoryRepository extends JpaRepository<TitleHistory, Integer> {
    List<TitleHistory> findByAccountId(Long accountId);
}
