package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.UpdateMemorizationRequest;
import com.wowraid.jobspoon.user_term.service.response.UpdateMemorizationResponse;

public interface MemorizationService {
    UpdateMemorizationResponse updateMemorization(UpdateMemorizationRequest request);
}
