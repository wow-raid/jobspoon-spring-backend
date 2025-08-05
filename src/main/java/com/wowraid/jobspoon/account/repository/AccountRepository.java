package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    long countByEmailStartingWith(String prefix);
    Optional<Account> findByEmail(String email);
}
