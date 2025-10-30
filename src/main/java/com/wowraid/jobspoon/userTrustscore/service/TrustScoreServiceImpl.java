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

    /**
     * 최초 계정 생성 시 기본 신뢰점수 레코드 생성
     */
    @Override
    @Transactional
    public void initTrustScore(Long accountId) {
        if (trustScoreRepository.existsByAccountId(accountId)) return;

        TrustScore init = new TrustScore(
                null,
                accountId,
                0.0,  // attendanceRate
                0.0,  // attendanceScore
                0.0,  // interviewScore
                0.0,  // problemScore
                0.0,  // studyroomScore
                0.0,  // totalScore
                LocalDateTime.now()
        );
        trustScoreRepository.save(init);
    }

    /**
     * DB에 저장된 최신 신뢰점수 조회
     */
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


    /**
     * 대시보드 데이터를 기반으로 신뢰점수를 계산 및 저장
     */
    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId) {
        // 대시보드 데이터 수집
        UserDashboardSummaryResponse summary = userDashboardService.getMonthlySummary(accountId);

        // 항목별 점수 계산
        double attendanceRate = summary.getAttendanceRate();
        double attendanceScore = attendanceRate * 40.0;
        double interviewScore = Math.min(summary.getMonthlyInterviews(), 10) / 10.0 * 25.0;
        double problemScore = Math.min(summary.getMonthlyProblems(), 20) / 20.0 * 25.0;
        double studyroomScore = Math.min(summary.getMonthlyStudyrooms(), 3) / 3.0 * 10.0;

        // 총점 계산
        double totalScore = Math.min(
                attendanceScore + interviewScore + problemScore + studyroomScore,
                100.0
        );

        // DB 반영
        TrustScore trustScore = trustScoreRepository.findByAccountId(accountId)
                .orElse(new TrustScore(
                        null, accountId,
                        0, 0, 0, 0, 0, 0,
                        LocalDateTime.now()
                ));

        trustScore.updateScores(
                attendanceRate,
                attendanceScore,
                interviewScore,
                problemScore,
                studyroomScore,
                totalScore
        );
        trustScoreRepository.save(trustScore);

        // 응답 변환
        return TrustScoreResponse.fromEntity(trustScore);
    }
}