package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;

public interface UserWordbookFolderService {
    CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request);
}
