package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserWordbookFolderRepository extends JpaRepository<UserWordbookFolder, Long> {

    // 같은 사용자 안에서 폴더명 중복 방지
    boolean existsByAccount_IdAndFolderName(Long accountId, String folderName);

    // 폴더 소유권 확인
    boolean existsByIdAndAccount_Id(Long folderId, Long accountId);

    // 정렬용 최댓값 (사용자별)
    @Query("select coalesce(max(f.sortOrder), -1) from UserWordbookFolder f where f.account.id = :accountId")
    int findMaxSortOrderByAccountId(@Param("accountId") Long accountId);




}
