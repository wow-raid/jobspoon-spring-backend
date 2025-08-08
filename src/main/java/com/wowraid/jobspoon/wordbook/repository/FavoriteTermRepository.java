package com.wowraid.jobspoon.wordbook.repository;

import com.wowraid.jobspoon.wordbook.entity.FavoriteTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTermRepository extends JpaRepository<FavoriteTerm, Long> {
//    boolean existsByAccountAndTerm(Account account, Term term);
}
