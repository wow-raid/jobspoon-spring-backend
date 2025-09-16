package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserTermProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserTermProgressRepository extends JpaRepository<UserTermProgress, UserTermProgress.Id> {
    List<UserTermProgress> findByIdAccountIdAndIdTermIdIn(Long accountId, Collection<Long> termIds);
}