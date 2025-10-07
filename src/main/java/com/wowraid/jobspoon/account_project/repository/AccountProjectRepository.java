package com.wowraid.jobspoon.account_project.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account_project.entity.AccountProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountProjectRepository extends JpaRepository<AccountProject, Long> {

    List<AccountProject> findAllByAccount_IdAndIsActiveTrue(Long accountId);

}
