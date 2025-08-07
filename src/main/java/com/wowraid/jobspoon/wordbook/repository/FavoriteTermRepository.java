package com.wowraid.jobspoon.wordbook.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.wordbook.entity.FavoriteTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTermRepository extends JpaRepository<FavoriteTerm, Long> {
    boolean existsByAccountAndTerm(Account account, Term term);
}
