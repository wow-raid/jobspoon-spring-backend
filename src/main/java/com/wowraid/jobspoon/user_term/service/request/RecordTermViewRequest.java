package com.wowraid.jobspoon.user_term.service.request;

import com.wowraid.jobspoon.user_term.entity.ViewSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecordTermViewRequest {
    private final Long accountId;     // 인증된 사용자 ID
    private final Long termId;        // 열람한 용어 ID
    private final ViewSource source;  // 열람 경로
}
