package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreHistoryResponse;
import com.wowraid.jobspoon.userTrustscore.entity.TrustScoreHistory;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustScoreHistoryServiceImpl implements TrustScoreHistoryService {

    private final TrustScoreHistoryRepository historyRepository;

    /**
     * 전달 말일 23:59:59 시점으로 월별 기록 저장
     * - 이미 이번 달 기록이 있으면 skip
     */
    @Override
    @Transactional
    public void recordMonthlyScore(Long accountId, double score) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 이번 달 기록이 이미 존재하면 중복 방지
        boolean exists = historyRepository.existsByAccountIdAndRecordedAtBetween(
                accountId,
                startOfMonth,
                startOfMonth.plusMonths(1).minusSeconds(1)
        );

        if (exists) {
            log.info("⏩ Skipped: accountId={} already has record for {}", accountId, startOfMonth.getMonth());
            return;
        }

        // 전달 말일 23:59:59 기준으로 기록
        LocalDateTime recordedAt = startOfMonth.minusSeconds(1);

        historyRepository.save(
                TrustScoreHistory.builder()
                        .accountId(accountId)
                        .totalScore(score)
                        .recordedAt(recordedAt)
                        .build()
        );

        log.info("📊 Recorded TrustScoreHistory: accountId={}, score={}, recordedAt={}",
                accountId, score, recordedAt);
    }

    /**
     * 히스토리 조회 (최신순)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrustScoreHistoryResponse> getHistory(Long accountId) {
        return historyRepository.findByAccountIdOrderByRecordedAtDesc(accountId)
                .stream()
                .map(TrustScoreHistoryResponse::fromEntity)
                .toList();
    }
}
