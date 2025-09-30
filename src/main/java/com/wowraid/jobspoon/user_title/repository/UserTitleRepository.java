package com.wowraid.jobspoon.user_title.repository;

import com.wowraid.jobspoon.user_title.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {
    List<UserTitle> findAllByAccountId(Long accountId);
    Optional<UserTitle> findByIdAndAccountId(Long titleId, Long accountId);
    void deleteAllByAccountId(Long accountId);
}
