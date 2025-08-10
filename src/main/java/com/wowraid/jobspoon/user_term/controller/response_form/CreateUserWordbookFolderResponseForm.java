package com.wowraid.jobspoon.user_term.controller.response_form;

import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateUserWordbookFolderResponseForm {
    private final String message;
    private final Long id;
    //    private final Long accountId;
    private final String folderName;
    private final Integer sortOrder;
    private final String createdAt;

    public static CreateUserWordbookFolderResponseForm from(CreateUserWordbookFolderResponse response) {
        return new CreateUserWordbookFolderResponseForm(
                response.getMessage(),
                response.getId(),
                response.getFolderName(),
                response.getSortOrder(),
                response.getCreatedAt()
        );
    }
}
