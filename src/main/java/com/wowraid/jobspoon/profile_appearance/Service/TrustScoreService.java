package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.TrustScoreResponse;

public interface TrustScoreService {
    TrustScoreResponse calculateTrustScore(Long accountId);
}
