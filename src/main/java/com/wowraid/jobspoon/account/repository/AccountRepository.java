package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
