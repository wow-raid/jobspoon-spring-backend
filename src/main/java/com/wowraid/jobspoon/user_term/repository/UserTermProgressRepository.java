package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserTermProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermProgressRepository extends JpaRepository<UserTermProgress, UserTermProgress.Id> {
}
