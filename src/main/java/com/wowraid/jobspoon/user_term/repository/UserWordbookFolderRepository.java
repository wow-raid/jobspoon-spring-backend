package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserWordbookFolderRepository extends JpaRepository<UserWordbookFolder, Long> {
    boolean existsByIdAndAccount_Id(Long folderId, Long accountId);

    @Query("select coalesce(max(f.sortOrder), -1) from UserWordbookFolder f where f.account.id = :accountId")
    int findMaxSortOrderByAccountId(@Param("accountId") Long accountId);

    boolean existsByAccount_IdAndNormalizedFolderName(Long accountId, String normalizedFolderName);
    List<UserWordbookFolder> findAllByAccount_Id(Long accountId);
    List<UserWordbookFolder> findAllByAccount_IdOrderBySortOrderAscIdAsc(Long accountId);

    boolean existsByAccount_IdAndNormalizedFolderNameAndIdNot(Long accountId, String folderName, Long excludeFolderId);
}
