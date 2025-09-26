package com.wowraid.jobspoon.user_trustscore.service;

import com.wowraid.jobspoon.user_trustscore.controller.response.TrustScoreResponse;

public interface TrustScoreService {
    // 단순 조회
    TrustScoreResponse getTrustScore(Long accountId);

    // 계산 + 저장
    TrustScoreResponse calculateTrustScore(Long accountId);
}
