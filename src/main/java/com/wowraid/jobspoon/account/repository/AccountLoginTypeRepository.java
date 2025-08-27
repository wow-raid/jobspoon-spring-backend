package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.AccountLoginType;
import com.wowraid.jobspoon.account.entity.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountLoginTypeRepository extends JpaRepository<AccountLoginType, Long> {
    Optional<AccountLoginType> findByLoginType(LoginType loginType);
}
