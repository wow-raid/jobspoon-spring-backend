package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.AccountRoleType;
import com.wowraid.jobspoon.account.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRoleTypeRepository extends JpaRepository<AccountRoleType, Long> {
    Optional<AccountRoleType> findByRoleEnum(RoleType roleEnum);
}