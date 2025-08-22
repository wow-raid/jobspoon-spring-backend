package com.wowraid.jobspoon.user_term.repository;

import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserWordbookFolderRepository extends JpaRepository<UserWordbookFolder, Long> {
//    boolean existsByAccount_IdAndFolderName(Long accountId, String folderName);
    boolean existsByFolderName(String folderName);

    // 폴더가 하나도 없어서 max 값이 null인 경우 -1 리턴
    // nextOrder = max + 1 = 0
    @Query("select coalesce(max(f.sortOrder), -1) from UserWordbookFolder f")
    int findGlobalMaxSortOrder();
}
