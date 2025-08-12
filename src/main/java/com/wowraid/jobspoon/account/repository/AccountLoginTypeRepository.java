package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.AccountLoginType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLoginTypeRepository extends JpaRepository<AccountLoginType, Long> {
}
