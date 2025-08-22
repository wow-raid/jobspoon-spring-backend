package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileAppearanceRepository extends JpaRepository<ProfileAppearance, Integer> {
    Optional<ProfileAppearance> findByAccountProfile_Account_Id(Long accountId);
}