package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserWordbookTermRepository extends JpaRepository<UserWordbookTerm, Long> {
    Optional<UserWordbookTerm> findByIdAndAccount_Id(Long id, Long accountId);

    @Query(
            value = """
      select uwt
      from UserWordbookTerm uwt
        join uwt.folder f
        join f.account a
        join fetch uwt.term t
      where f.id = :folderId
        and a.id = :accountId
      """,
            countQuery = """
      select count(uwt)
      from UserWordbookTerm uwt
        join uwt.folder f
        join f.account a
      where f.id = :folderId
        and a.id = :accountId
      """
    )
    Page<UserWordbookTerm> findPageByFolderAndOwnerFetch(
            @Param("folderId") Long folderId,
            @Param("accountId") Long accountId,
            Pageable pageable
    );

    List<UserWordbookTerm> account(Account account);
    Optional<UserWordbookTerm> findByAccount_IdAndFolder_IdAndTerm_Id(Long folderId, Long accountId, Long termId);
}
