package com.wowraid.jobspoon.user_term.service.response;

import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserWordbookFolderResponse {
    private final Long id;
    private final String folderName;
    private final Integer sortOrder;
    private final String message;
    private final String createdAt;

    public static CreateUserWordbookFolderResponse from(UserWordbookFolder f) {
        return CreateUserWordbookFolderResponse.builder()
                .id(f.getId())
                .folderName(f.getFolderName())
                .sortOrder(f.getSortOrder())
                .message("폴더가 생성되었습니다.")
                .createdAt(f.getCreatedAt().toString())
                .build();
    }
}
