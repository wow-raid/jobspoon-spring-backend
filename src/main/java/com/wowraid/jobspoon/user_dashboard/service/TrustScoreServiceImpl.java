package com.wowraid.jobspoon.user_dashboard.service;

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
    public double calculateTrustScore(Long accountId) {
        double score = 0;

        // 출석률 (최대 25점)
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        score += attendanceRate * 0.25;

        // 모의면접 (최대 20점)
        var interview = interviewSummaryService.getCompletionStatus(accountId);
        score += Math.min(interview.getInterviewTotalCount() * 0.5
                + interview.getInterviewMonthlyCount() * 2, 20);

        // 문제풀이 (최대 20점)
        long quizTotal = quizSummaryService.getTotalCount(accountId);
        long quizMonthly = quizSummaryService.getMonthlyCount(accountId);
        score += Math.min(quizTotal * 0.3 + quizMonthly * 1.5, 20);

        // 글쓰기 (리뷰/스터디룸/댓글)
        long reviewCount = writingCountService.getReviewCount(accountId);
        long studyroomCount = writingCountService.getStudyroomCount(accountId);
        long commentCount = writingCountService.getCommentCount(accountId);

        score += Math.min(reviewCount * 2, 10);
        score += Math.min(studyroomCount * 5, 10);
        score += Math.min(commentCount * 0.5, 10);

        boolean hasRecentActivity = interview.getInterviewMonthlyCount() > 0
                || quizMonthly > 0
                || reviewCount > 0
                || commentCount > 0;

        if(hasRecentActivity) {
            score += 5;
        }

        return Math.min(score, 100);
    }
}
