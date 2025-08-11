package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWordbookTermRepository extends JpaRepository<UserWordbookTerm, Long> {
//    Optional<UserWordbookTerm> findByIdandAccount_Id(Long id, Long accountId);
}
