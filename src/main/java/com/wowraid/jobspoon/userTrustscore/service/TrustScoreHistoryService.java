package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreHistoryResponse;

import java.util.List;

public interface TrustScoreHistoryService {

    void recordMonthlyScore(Long accountId, double score);
    List<TrustScoreHistoryResponse> getHistory(Long accountId);
}
