package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.AttendanceRateResponse;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.InterviewCompletionResponse;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.QuizCompletionResponse;
import com.wowraid.jobspoon.user_dashboard.service.AttendanceService;
import com.wowraid.jobspoon.user_dashboard.service.InterviewSummaryService;
import com.wowraid.jobspoon.user_dashboard.service.QuizSummaryService;
import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-dashboard")
@RequiredArgsConstructor
public class UserDashboardController {

    private final TokenAccountService tokenAccountService;
    private final AttendanceService attendanceService;
    private final InterviewSummaryService interviewSummaryService;
    private final QuizSummaryService quizSummaryService;

    @GetMapping("/attendance/rate")
    public ResponseEntity<AttendanceRateResponse> getRate(@RequestHeader("Authorization") String userToken){

        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AttendanceRateResponse response = attendanceService.getThisMonthRate(accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/interview/completion")
    public ResponseEntity<InterviewCompletionResponse> getInterviewCompletion(@RequestHeader("Authorization") String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        InterviewCompletionResponse response = interviewSummaryService.getCompletionStatus(accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("quiz/completion")
    public ResponseEntity<QuizCompletionResponse> getQuizCompletion(@RequestHeader("Authorization") String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        QuizCompletionResponse response = new QuizCompletionResponse(
                quizSummaryService.getTotalCount(accountId),
                quizSummaryService.getMonthlyCount(accountId)
        );

        return ResponseEntity.ok(response);
    }
}