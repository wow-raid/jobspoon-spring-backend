package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 대상 폴더에 이미 같은 term이 있는지 여부
    boolean existsByAccount_IdAndFolder_IdAndTerm_Id(Long accountId, Long folderId, Long termId);

    // 필요시 단건 조회(파라미터 순서: accountId, folderId, termId)
    Optional<UserWordbookTerm> findByAccount_IdAndFolder_IdAndTerm_Id(Long accountId, Long folderId, Long termId);

    // 대상 폴더의 마지막 sortOrder 엔트리(내림차순 1건)
    Optional<UserWordbookTerm> findTopByAccount_IdAndFolder_IdOrderBySortOrderDesc(Long accountId, Long folderId);

    // 최대 sortOrder를 바로 가져오기(게터 없이도 사용 가능)
    @Query("""
        select coalesce(max(uwt.sortOrder), 0)
        from UserWordbookTerm uwt
        where uwt.account.id = :accountId
          and uwt.folder.id  = :folderId
    """)
    Integer findMaxSortOrderByAccountAndFolder(@Param("accountId") Long accountId,
                                               @Param("folderId") Long folderId);
}
