package com.wowraid.jobspoon.user_trustscore.scheduler;

import com.wowraid.jobspoon.user_trustscore.entity.TrustScore;
import com.wowraid.jobspoon.user_trustscore.repository.TrustScoreRepository;
import com.wowraid.jobspoon.user_trustscore.service.TrustScoreHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrustScoreScheduler {

    private final TrustScoreRepository trustScoreRepository;
    private final TrustScoreHistoryService historyService;

    /**
     * 매달 1일 새벽 0시 5분에 전체 유저 대상으로 실행
     * account_id | score | recorded_at
     * -----------+-------+---------------------
     * 1          | 72.5  | 2025-09-30 23:59:59
     * 2          | 61.0  | 2025-09-30 23:59:59
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    public void archiveMonthlyScores() {
        List<TrustScore> all = trustScoreRepository.findAll();
        for (TrustScore ts : all) {
            historyService.recordMonthlyScore(ts.getAccountId(), ts.getScore());
        }
    }
}
