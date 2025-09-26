package com.wowraid.jobspoon.user_trustscore.service;

import com.wowraid.jobspoon.user_trustscore.controller.response.TrustScoreHistoryResponse;

import java.util.List;

public interface TrustScoreHistoryService {

    void recordMonthlyScore(Long accountId, double score);
    List<TrustScoreHistoryResponse> getHistory(Long accountId);
}
