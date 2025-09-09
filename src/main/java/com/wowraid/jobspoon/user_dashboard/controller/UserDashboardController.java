package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.AttendanceRateResponse;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.InterviewCompletionResponse;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.QuizCompletionResponse;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.WritingCountResponse;
import com.wowraid.jobspoon.user_dashboard.service.*;
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
    private final WritingCountService writingCountService;

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

    @GetMapping("/quiz/completion")
    public ResponseEntity<QuizCompletionResponse> getQuizCompletion(@RequestHeader("Authorization") String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        QuizCompletionResponse response = new QuizCompletionResponse(
                quizSummaryService.getTotalCount(accountId),
                quizSummaryService.getMonthlyCount(accountId)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/writing/count")
    public ResponseEntity<WritingCountResponse> getWritingCount(@RequestHeader("Authorization") String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);

        long reviewCount = writingCountService.getReviewCount(accountId);
        long studyroomCount = writingCountService.getStudyroomCount(accountId);
        long commentCount = writingCountService.getCommentCount(accountId);

        WritingCountResponse response = new WritingCountResponse(
                reviewCount,
                studyroomCount,
                commentCount,
                reviewCount + studyroomCount + commentCount   // 👈 총 작성 횟수
        );

        return ResponseEntity.ok(response);
    }
}