package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.TrustScore;
import com.wowraid.jobspoon.profile_appearance.Repository.TrustScoreRepository;
import com.wowraid.jobspoon.attendance.service.AttendanceService;
import com.wowraid.jobspoon.user_dashboard.service.QuizSummaryService;
import com.wowraid.jobspoon.user_dashboard.service.WritingCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {
    private final AttendanceService attendanceService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;
    private final TrustScoreRepository trustScoreRepository;

    @Override
    @Transactional
    public TrustScoreResponse calculateTrustScore(Long accountId){
        // 데이터 수집
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        long monthlyProblems = quizSummaryService.getMonthlyCount(accountId);
        long monthlyPosts = writingCountService.getPostsCount(accountId);
        long monthlyStudyrooms = writingCountService.getStudyroomsCount(accountId);
        long monthlyComments = writingCountService.getCommentsCount(accountId);

        // 인터뷰는 아직 도메인 없음 → 임시로 0
        long monthlyInterviews = 0L;

        // 점수 산정
        double attendanceScore = calcAttendanceScore(attendanceRate);
        double problemScore = calcProblemScore(monthlyProblems);
        double postScore = calcPostScore(monthlyPosts);
        double studyroomScore = calcStudyroomScore(monthlyStudyrooms);
        double commentScore = calcCommentScore(monthlyComments);
        double interviewScore = 0; // 인터뷰 점수도 현재는 0

        double totalScore = attendanceScore
                + interviewScore
                + problemScore
                + postScore
                + studyroomScore
                + commentScore;

        // 저장
        TrustScore trustScore = new TrustScore(
                null,
                accountId,
                attendanceRate,
                (int) monthlyInterviews,
                (int) monthlyProblems,
                (int) monthlyStudyrooms,
                (int) monthlyComments,
                (int) monthlyPosts,
                totalScore,
                LocalDateTime.now()
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
