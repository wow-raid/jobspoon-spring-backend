package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FavoriteTermRepository extends JpaRepository<FavoriteTerm, Long> {
    List<FavoriteTerm> account(Account account);
    boolean existsByAccountAndTerm(Account account, Term term);
    Optional<FavoriteTerm> findByAccount_IdAndTerm_Id(Long accountId, Long termId);
    List<FavoriteTerm> findAllByIdIn(Collection<Long> ids);
}
