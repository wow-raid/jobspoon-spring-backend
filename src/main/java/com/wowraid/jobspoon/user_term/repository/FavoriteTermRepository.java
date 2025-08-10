package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTermRepository extends JpaRepository<FavoriteTerm, Long> {
//    boolean existsByAccountAndTerm(Account account, Term term);
}
