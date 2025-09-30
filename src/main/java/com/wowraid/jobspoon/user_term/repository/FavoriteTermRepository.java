package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteTermRepository extends JpaRepository<FavoriteTerm, Long> {
    boolean existsByAccountAndTerm(Account account, Term term);
    Optional<FavoriteTerm> findByAccount_IdAndTerm_Id(Long accountId, Long termId);
    void deleteAllByIdInBatch(Iterable<Long> ids);

    @Query("""
        select ft.term
        from FavoriteTerm ft
        where ft.account.id = :accountId
    """)
    List<Term> findTermsByAccount(@Param("accountId") Long accountId);

    @Query("""
        select ft.term
        from FavoriteTerm ft
        where ft.account.id = :accountId
          and (:folderId is null or ft.folder.id = :folderId)
    """)
    List<Term> findTermsByAccountAndFolder(@Param("accountId") Long accountId,
                                           @Param("folderId") Long folderId);
}
