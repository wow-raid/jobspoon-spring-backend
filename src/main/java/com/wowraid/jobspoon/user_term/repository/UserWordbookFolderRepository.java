// UserWordbookFolderRepository.java
package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserWordbookFolderRepository extends JpaRepository<UserWordbookFolder, Long> {
    boolean existsByIdAndAccount_Id(Long folderId, Long accountId);

    @Query("select coalesce(max(f.sortOrder), -1) from UserWordbookFolder f where f.account.id = :accountId")
    int findMaxSortOrderByAccountId(@Param("accountId") Long accountId);

    boolean existsByAccount_IdAndNormalizedFolderName(Long accountId, String normalizedFolderName);
}
