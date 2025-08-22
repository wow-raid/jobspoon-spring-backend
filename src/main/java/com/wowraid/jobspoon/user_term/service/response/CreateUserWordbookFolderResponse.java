package com.wowraid.jobspoon.user_term.service.response;

import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateUserWordbookFolderResponse {

    private final String message;
    private final Long id;
//    private final Long accountId;
    private final String folderName;
    private final Integer sortOrder;
    private final String createdAt;

    public static CreateUserWordbookFolderResponse from(UserWordbookFolder folder) {
        String message = "단어장 폴더를 성공적으로 만들었습니다.";
        return new CreateUserWordbookFolderResponse(message, folder.getId(), folder.getFolderName(), folder.getSortOrder(), folder.getCreatedAt().toString());
    }
}
