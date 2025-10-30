package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.userTrustscore.entity.TrustScore;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreRepository;
import com.wowraid.jobspoon.userAttendance.service.AttendanceService;
import com.wowraid.jobspoon.userDashboard.service.QuizSummaryService;
import com.wowraid.jobspoon.userDashboard.service.WritingCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {

    private final AttendanceService attendanceService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;
    private final InterviewService interviewService;
    private final TrustScoreRepository trustScoreRepository;

    /**
     * 회원가입 시 초기화 (기본 row 생성)
     */
    @Override
    @Transactional
    public void initTrustScore(Long accountId) {
        if (trustScoreRepository.existsByAccountId(accountId)) {
            log.info("[initTrustScore] 이미 존재하는 accountId={}", accountId);
            return;
        }

        TrustScore init = new TrustScore(
                null,        // id
                accountId,
                0,  // attendanceRate
                0,              // monthlyInterviews
                0,              // monthlyProblems
                0,              // monthlyStudyrooms
                0,              // score
                LocalDateTime.now()
        );

        trustScoreRepository.save(init);
        log.info("[initTrustScore] 기본 신뢰점수 생성됨: accountId={}", accountId);
    }

    /**
     * 신뢰점수 조회 (없으면 생성)
     */
    @Override
    @Transactional(readOnly = true)
    public TrustScoreResponse getTrustScore(Long accountId) {
        TrustScore entity = trustScoreRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    log.warn("[getTrustScore] 데이터 없음 → initTrustScore 호출: accountId={}", accountId);
                    initTrustScore(accountId);
                    return trustScoreRepository.findByAccountId(accountId).orElseThrow();
                });

        return TrustScoreResponse.fromEntity(entity);
    }

    /**
     * 신뢰점수 계산 및 갱신
     */
    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId) {
        // 데이터 수집
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        int monthlyProblems = (int) quizSummaryService.getMonthlyCount(accountId);
        int monthlyStudyrooms = (int) writingCountService.getStudyroomsCount(accountId);
        int monthlyInterviews = interviewService.getMonthlyFinishedCount(accountId);

        // 점수 계산 (4개 항목)
        double totalScore = calculateScore(attendanceRate, monthlyInterviews, monthlyProblems, monthlyStudyrooms);

        // DB 반영
        TrustScore trustScore = trustScoreRepository.findByAccountId(accountId)
                .orElse(new TrustScore(null, accountId, 0, 0, 0, 0, 0, LocalDateTime.now()));

        trustScore.update(attendanceRate, monthlyInterviews, monthlyProblems, monthlyStudyrooms, totalScore);
        trustScoreRepository.save(trustScore);

        log.info("[calculateTrustScore] accountId={} → totalScore={}", accountId, totalScore);
        return TrustScoreResponse.fromEntity(trustScore);
    }

    // ========================= 내부 계산 메소드 =========================
    private double calculateScore(double attendanceRate, int monthlyInterviews, int monthlyProblems, int monthlyStudyrooms) {
        double attendanceScore = attendanceRate * 40.0; // 출석률 * 40
        double interviewScore = Math.min(monthlyInterviews, 10) / 10.0 * 25.0;
        double problemScore = Math.min(monthlyProblems, 20) / 20.0 * 25.0;
        double studyroomScore = Math.min(monthlyStudyrooms, 3) / 3.0 * 10.0;

        return Math.min(attendanceScore + interviewScore + problemScore + studyroomScore, 100.0);
    }
}