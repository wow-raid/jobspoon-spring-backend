package com.wowraid.jobspoon.user_term.service.request;

import com.wowraid.jobspoon.user_term.entity.MemorizationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateMemorizationRequest {
    private final Long accountId;
    private final Long termId;
    private final MemorizationStatus status;
}
