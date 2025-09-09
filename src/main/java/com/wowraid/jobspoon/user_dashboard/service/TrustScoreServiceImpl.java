package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.TrustScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrustScoreServiceImpl implements TrustScoreService {

    private final AttendanceService attendanceService;
    private final InterviewSummaryService interviewSummaryService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;

    @Override
    public TrustScoreResponse calculateTrustScore(Long accountId) {
        double score = 0;

        // 출석률 (최대 25점)
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        double attendanceScore = Math.min(attendanceRate * 0.25, 25);
        score += attendanceScore;

        // 모의면접 (최대 20점)
        var interview = interviewSummaryService.getCompletionStatus(accountId);
        double interviewScore = Math.min(interview.getInterviewTotalCount() * 0.5
                + interview.getInterviewMonthlyCount() * 2, 20);
        score += interviewScore;

        // 문제풀이 (최대 20점)
        long quizTotal = quizSummaryService.getTotalCount(accountId);
        long quizMonthly = quizSummaryService.getMonthlyCount(accountId);
        double quizScore = Math.min(quizTotal * 0.3 + quizMonthly * 1.5, 20);
        score += quizScore;

        // 글쓰기 (리뷰/스터디룸/댓글)
        long reviewCount = writingCountService.getReviewCount(accountId);
        long studyroomCount = writingCountService.getStudyroomCount(accountId);
        long commentCount = writingCountService.getCommentCount(accountId);

        double reviewScore = Math.min(reviewCount * 2, 10);
        double studyroomScore = Math.min(studyroomCount * 5, 10);
        double commentScore = Math.min(commentCount * 0.5, 10);

        score += reviewScore + studyroomScore + commentScore;

        // 보너스
        boolean bonusApplied = interview.getInterviewMonthlyCount() > 0
                || quizMonthly > 0
                || reviewCount > 0
                || commentCount > 0;

        if (bonusApplied) {
            score += 5;
        }

        double finalScore = Math.min(score, 100);

        return new TrustScoreResponse(
                finalScore,
                attendanceScore,
                interviewScore,
                quizScore,
                reviewScore,
                studyroomScore,
                commentScore,
                bonusApplied
        );
    }
}
