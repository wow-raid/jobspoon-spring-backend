package com.wowraid.jobspoon.user_term.service.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddUserWordbookTermRequest {
    private Long accountId;
    private Long folderId;
    private Long termId;
}
