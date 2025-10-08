package com.wowraid.jobspoon.profileAppearance.Repository;

import com.wowraid.jobspoon.profileAppearance.Entity.ProfileAppearance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileAppearanceRepository extends JpaRepository<ProfileAppearance, Long> {
    Optional<ProfileAppearance> findByAccountId(Long accountId);
    void deleteByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
}