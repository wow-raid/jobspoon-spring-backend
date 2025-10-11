package com.wowraid.jobspoon.userTrustscore.service;

import com.wowraid.jobspoon.userTrustscore.controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.userTrustscore.entity.TrustScore;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreRepository;
import com.wowraid.jobspoon.userAttendance.service.AttendanceService;
import com.wowraid.jobspoon.userDashboard.service.QuizSummaryService;
import com.wowraid.jobspoon.userDashboard.service.WritingCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {

    private final AttendanceService attendanceService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;
    private final TrustScoreRepository trustScoreRepository;

    @Override
    @Transactional(readOnly = true)
    public TrustScoreResponse getTrustScore(Long accountId) {
        return trustScoreRepository.findByAccountId(accountId)
                .map(TrustScoreResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("No trust score found for account id: " + accountId));
    }

    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId){
        // 데이터 수집
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        long monthlyProblems = quizSummaryService.getMonthlyCount(accountId);
        long monthlyPosts = writingCountService.getPostsCount(accountId);
        long monthlyStudyrooms = writingCountService.getStudyroomsCount(accountId);
        long monthlyComments = writingCountService.getCommentsCount(accountId);
        long monthlyInterviews = 0L; // 인터뷰 도메인 미구현 상태

        // 점수 산정 (인터뷰 미포함)
        double totalScore = calcAttendanceScore(attendanceRate)
                + calcProblemScore(monthlyProblems)
                + calcPostScore(monthlyPosts)
                + calcStudyroomScore(monthlyStudyrooms)
                + calcCommentScore(monthlyComments);

        // 점수 조회 or 새로 생성
        TrustScore trustScore = trustScoreRepository.findByAccountId(accountId)
                .orElse(new TrustScore(null, accountId, 0,0,0,0,0,0,0,null));

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

        return TrustScoreResponse.fromEntity(trustScore);
    }

    // ===== 점수 산정 메소드 =====
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
