package com.wowraid.jobspoon.account_profile.repository;

import com.wowraid.jobspoon.account_profile.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {
}