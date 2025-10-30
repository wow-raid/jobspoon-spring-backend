package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.userDashboard.controller.response.UserDashboardSummaryResponse;
import com.wowraid.jobspoon.userDashboard.service.UserDashboardService;
import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.userTrustscore.entity.TrustScore;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {

    private final UserDashboardService  userDashboardService;
    private final TrustScoreRepository trustScoreRepository;

    @Override
    @Transactional
    public void initTrustScore(Long accountId) {
        if (trustScoreRepository.existsByAccountId(accountId)) return;

        TrustScore init = new TrustScore(
                null, accountId,
                0, 0, 0, 0, 0,
                LocalDateTime.now()
        );
        trustScoreRepository.save(init);
    }

    @Override
    @Transactional(readOnly = true)
    public TrustScoreResponse getTrustScore(Long accountId) {
        TrustScore entity = trustScoreRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    initTrustScore(accountId);
                    return trustScoreRepository.findByAccountId(accountId).orElseThrow();
                });

        return TrustScoreResponse.fromEntity(entity);
    }

    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId) {
        // 대시보드 요약 데이터 한 번에 수집
        UserDashboardSummaryResponse summary = userDashboardService.getMonthlySummary(accountId);

        double totalScore = calculateScore(
                summary.getAttendanceRate(),
                summary.getMonthlyInterviews(),
                summary.getMonthlyProblems(),
                summary.getMonthlyStudyrooms()
        );

        TrustScore trustScore = trustScoreRepository.findByAccountId(accountId)
                .orElse(new TrustScore(null, accountId, 0, 0, 0, 0, 0, LocalDateTime.now()));

        trustScore.update(
                summary.getAttendanceRate(),
                summary.getMonthlyInterviews(),
                summary.getMonthlyProblems(),
                summary.getMonthlyStudyrooms(),
                totalScore
        );
        trustScoreRepository.save(trustScore);

        return TrustScoreResponse.fromEntity(trustScore);
    }

    private double calculateScore(double attendanceRate, int monthlyInterviews, int monthlyProblems, int monthlyStudyrooms) {
        double attendanceScore = attendanceRate * 40.0;
        double interviewScore = Math.min(monthlyInterviews, 10) / 10.0 * 25.0;
        double problemScore = Math.min(monthlyProblems, 20) / 20.0 * 25.0;
        double studyroomScore = Math.min(monthlyStudyrooms, 3) / 3.0 * 10.0;
        return Math.min(attendanceScore + interviewScore + problemScore + studyroomScore, 100.0);
    }
}