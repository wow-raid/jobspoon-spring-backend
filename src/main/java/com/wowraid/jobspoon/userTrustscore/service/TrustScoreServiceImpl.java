package com.wowraid.jobspoon.userTrustscore.service;

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
    private final TrustScoreRepository trustScoreRepository;

    /**
     *  회원가입 시 초기화 (기본 row 생성)
     */
    @Override
    @Transactional
    public void initTrustScore(Long accountId) {
        if (trustScoreRepository.existsByAccountId(accountId)) {
            log.info("[initTrustScore] 이미 존재하는 accountId={}", accountId);
            return;
        }

        TrustScore init = new TrustScore(
                null,           // id (auto)
                accountId,      // accountId
                0,              // attendanceRate
                0,              // monthlyInterviews
                0,              // monthlyProblems
                0,              // monthlyStudyrooms
                0,              // monthlyComments
                0,              // monthlyPosts
                0,              // totalScore
                LocalDateTime.now()            // calculatedAt
        );

        trustScoreRepository.save(init);
        log.info("[initTrustScore] 기본 신뢰점수 생성됨: accountId={}", accountId);
    }

    /**
     *  신뢰점수 조회 (없으면 생성)
     */
    @Override
    @Transactional
    public TrustScoreResponse getTrustScore(Long accountId) {
        TrustScore entity = trustScoreRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    log.warn("[getTrustScore] 기존 데이터 없음 → initTrustScore 호출: accountId={}", accountId);
                    initTrustScore(accountId);
                    return trustScoreRepository.findByAccountId(accountId).orElseThrow();
                });

        return TrustScoreResponse.fromEntity(entity);
    }

    /**
     *  신뢰점수 계산 및 갱신
     */
    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId){
        // 데이터 수집
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        long monthlyProblems = quizSummaryService.getMonthlyCount(accountId);
        long monthlyPosts = 0L;
        long monthlyStudyrooms = writingCountService.getStudyroomsCount(accountId);
        long monthlyComments = 0L;
        long monthlyInterviews = 0L; // 인터뷰 도메인 미구현 상태

        // 점수 산정
        double totalScore = calcAttendanceScore(attendanceRate)
                + calcProblemScore(monthlyProblems)
                + calcPostScore(monthlyPosts)
                + calcStudyroomScore(monthlyStudyrooms)
                + calcCommentScore(monthlyComments);

        // 기존 row 조회 or 생성
        TrustScore trustScore = trustScoreRepository.findByAccountId(accountId)
                .orElse(new TrustScore(null, accountId, 0,0,0,0,0,0,0, LocalDateTime.now()));

        // 값 갱신
        trustScore.update(
                attendanceRate,
                (int) monthlyInterviews,
                (int) monthlyProblems,
                (int) monthlyStudyrooms,
                (int) monthlyComments,
                (int) monthlyPosts,
                totalScore
        );

        trustScoreRepository.save(trustScore);
        log.info("[calculateTrustScore] accountId={} → totalScore={}", accountId, totalScore);

        return TrustScoreResponse.fromEntity(trustScore);
    }

    // ===== 내부 계산 메소드 =====
    private double calcAttendanceScore(double rate) {
        return Math.min(rate * 0.25, 25); // 100% = 25점
    }

    private double calcProblemScore(long count) {
        return Math.min(count, 20) * 1; // 문제풀이 1회 = 1점 (최대 20점)
    }

    private double calcPostScore(long count) {
        return Math.min(count, 10) * 1.5; // 글 작성 1회 = 1.5점 (최대 15점)
    }

    private double calcStudyroomScore(long count) {
        return Math.min(count, 5) * 2; // 스터디룸 개설 1회 = 2점 (최대 10점)
    }

    private double calcCommentScore(long count) {
        return Math.min(count, 30) * 0.5; // 댓글 1개 = 0.5점 (최대 15점)
    }
}