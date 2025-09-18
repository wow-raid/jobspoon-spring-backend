package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserWordbookTermRepository extends JpaRepository<UserWordbookTerm, Long> {

    //조회(목록) - 폴더 소유자 기준 페이지 조회
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

    //이동 로직(FOLDER-ONLY, account 불일치 데이터에도 견고)
    // 대상 폴더에 같은 term 존재 여부 (account 조건 제외)
    boolean existsByFolder_IdAndTerm_Id(Long folderId, Long termId);

    // 소스 폴더에 (folder+term) 존재 여부 및 식별자 획득
    Optional<UserWordbookTerm> findByFolder_IdAndTerm_Id(Long folderId, Long termId);

    // 소스에서 삭제 (folder+term 기준)
    void deleteByFolder_IdAndTerm_Id(Long folderId, Long termId);

    // 대상 폴더의 마지막 정렬 엔트리
    Optional<UserWordbookTerm> findTopByFolder_IdOrderBySortOrderDesc(Long folderId);

    // 대상 폴더의 최대 sortOrder 값만 조회
    @Query("""
        select coalesce(max(uwt.sortOrder), 0)
        from UserWordbookTerm uwt
        where uwt.folder.id = :folderId
    """)
    Integer findMaxSortOrderByFolder(@Param("folderId") Long folderId);

    /** (호환용) 기존 account-scoped 메서드들
     * - 기존 코드가 쓰는 곳이 있으면 유지
     * - '이동' 로직에서는 사용하지 말 것
     */
    Optional<UserWordbookTerm> findByIdAndAccount_Id(Long id, Long accountId);

    boolean existsByAccount_IdAndFolder_IdAndTerm_Id(Long accountId, Long folderId, Long termId);

    Optional<UserWordbookTerm> findByAccount_IdAndFolder_IdAndTerm_Id(Long accountId, Long folderId, Long termId);

    Optional<UserWordbookTerm> findTopByAccount_IdAndFolder_IdOrderBySortOrderDesc(Long accountId, Long folderId);

    @Query("""
        select coalesce(max(uwt.sortOrder), 0)
        from UserWordbookTerm uwt
        where uwt.account.id = :accountId
          and uwt.folder.id  = :folderId
    """)
    Integer findMaxSortOrderByAccountAndFolder(@Param("accountId") Long accountId,
                                               @Param("folderId") Long folderId);
}
