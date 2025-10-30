package com.wowraid.jobspoon.userDashboard.service;

import com.wowraid.jobspoon.userAttendance.service.AttendanceService;
import com.wowraid.jobspoon.userDashboard.controller.response.UserDashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDashboardServiceImpl implements UserDashboardService {

    private final AttendanceService attendanceService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;
    private final InterviewSummaryService interviewSummaryService;

    @Override
    @Transactional(readOnly = true)
    public UserDashboardSummaryResponse getMonthlySummary(Long accountId) {
        double attendanceRate = attendanceService.getThisMonthRate(accountId).getAttendanceRate();
        long monthlyProblems = quizSummaryService.getMonthlyCount(accountId);
        long monthlyStudyrooms = writingCountService.getStudyroomsCount(accountId);
        long monthlyInterviews = interviewSummaryService.getMonthlyFinishedCount(accountId);

        return new UserDashboardSummaryResponse(
                attendanceRate,
                (int) monthlyInterviews,
                (int) monthlyProblems,
                (int) monthlyStudyrooms
        );
    }
}
