package com.wowraid.jobspoon.user_term.service.request;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateUserWordbookFolderRequest {

    private final Long accountId;
    private final String folderName;

    public UserWordbookFolder toUserWordbookFolder(Integer sortOrder) {
        Account account = new Account(accountId);
        return new UserWordbookFolder(account, folderName, sortOrder);
    }
}

