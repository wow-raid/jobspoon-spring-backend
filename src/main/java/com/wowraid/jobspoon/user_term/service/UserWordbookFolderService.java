package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.*;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookTermResponse;
import com.wowraid.jobspoon.user_term.service.response.ListUserWordbookTermResponse;

public interface UserWordbookFolderService {
    CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request);
    ListUserWordbookTermResponse list(ListUserWordbookTermRequest request);
    void reorder(ReorderUserWordbookFoldersRequest request);
    CreateUserWordbookTermResponse attachTerm(CreateUserWordbookTermRequest request);
}
