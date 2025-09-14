package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.request.ListUserWordbookTermRequest;
import com.wowraid.jobspoon.user_term.service.request.ReorderUserWordbookFoldersRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import com.wowraid.jobspoon.user_term.service.response.ListUserWordbookTermResponse;

public interface UserWordbookFolderService {
    CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request);
    ListUserWordbookTermResponse list(ListUserWordbookTermRequest request);
    void reorder(ReorderUserWordbookFoldersRequest request);
}
