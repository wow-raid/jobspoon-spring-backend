package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreHistoryResponse;
import com.wowraid.jobspoon.userTrustscore.entity.TrustScoreHistory;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrustScoreHistoryServiceImpl implements TrustScoreHistoryService {

    private final TrustScoreHistoryRepository historyRepository;

    /**
     * 월별 기록을 1번만 저장
     */
    @Override
    @Transactional
    public void recordMonthlyScore(Long accountId, double score) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 이번 달 기록이 존재하는지 체크
        boolean exists = historyRepository.existsByAccountIdAndRecordedAtBetween(
                accountId,
                startOfMonth,
                startOfMonth.plusMonths(1).minusSeconds(1)
        );

        // 존재하지 않으면 값 삽입
        if(!exists) {
            historyRepository.save(
                    TrustScoreHistory.builder()
                            .accountId(accountId)
                            .score(score)
                            .recordedAt(now)
                            .build()
            );
        }
    }

    /**
     * 히스토리 조회
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
