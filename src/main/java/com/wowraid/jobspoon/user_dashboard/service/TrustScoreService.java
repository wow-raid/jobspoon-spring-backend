package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.TrustScoreResponse;

public interface TrustScoreService {
    TrustScoreResponse calculateTrustScore(Long accountId);
}
