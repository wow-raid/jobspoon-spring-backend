package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.TitleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TitleHistoryRepository extends JpaRepository<TitleHistory, Long> {
    List<TitleHistory> findAllByAccount_Id(Long accountId);
    Optional<TitleHistory> findByIdAndAccount_Id(Long titleId, Long accountId);
    void deleteAllByAccount_Id(Long accountId);
}
