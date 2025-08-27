package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.RankHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankHistoryRepository extends JpaRepository<RankHistory, Long> {
    List<RankHistory> findAllByAccount_Id(Long accountId);
    Optional<RankHistory> findByIdAndAccount_Id(Long rankId, Long accountId);
}
