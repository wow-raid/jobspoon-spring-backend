package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserRecentTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRecentTermRepository extends JpaRepository<UserRecentTerm, Long> {
    Optional<UserRecentTerm> findByAccountIdAndTerm_Id(Long accountId, Long termId);
}
