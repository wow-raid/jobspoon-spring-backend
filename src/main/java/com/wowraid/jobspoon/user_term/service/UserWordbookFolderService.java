package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.*;
import com.wowraid.jobspoon.user_term.service.response.*;

import java.util.List;

public interface UserWordbookFolderService {
    CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request);
    ListUserWordbookTermResponse list(ListUserWordbookTermRequest request);
    void reorder(ReorderUserWordbookFoldersRequest request);
    CreateUserWordbookTermResponse attachTerm(CreateUserWordbookTermRequest request);
    MoveFolderTermsResponse moveTerms(Long accountId, Long sourceFolderId, Long targetFolderId, List<Long> termIds);
    RenameUserWordbookFolderResponse rename(RenameUserWordbookFolderRequest request);
}
