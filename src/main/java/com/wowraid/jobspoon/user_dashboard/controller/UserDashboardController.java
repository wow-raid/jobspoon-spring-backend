package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.attendance.service.AttendanceService;
import com.wowraid.jobspoon.user_dashboard.controller.response.*;
import com.wowraid.jobspoon.user_dashboard.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-dashboard")
@RequiredArgsConstructor
public class UserDashboardController {

    private final TokenAccountService tokenAccountService;
    private final AttendanceService attendanceService;
    private final QuizSummaryService quizSummaryService;
    private final WritingCountService writingCountService;

    /**
     * 이번 달 출석률 조회
     * - attended: 이번 달 출석 일수
     * - totalDays: 이번 달 총 일수
     * - attendanceRate: 출석률 (attended / totalDays)
     */
    @GetMapping("/attendance/rate")
    public ResponseEntity<AttendanceRateResponse> getRate(@CookieValue(name = "userToken", required = false) String userToken){

        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AttendanceRateResponse response = attendanceService.getThisMonthRate(accountId);

        return ResponseEntity.ok(response);
    }

    /**
     * 문제풀이(퀴즈) 완료 현황 조회
     * - totalCount: 누적 완료 수
     * - monthlyCount: 이번 달 완료 수
     */
    @GetMapping("/quiz/completion")
    public ResponseEntity<QuizCompletionResponse> getQuizCompletion(@CookieValue(name = "userToken", required = false) String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        QuizCompletionResponse response = new QuizCompletionResponse(
                quizSummaryService.getTotalCount(accountId),
                quizSummaryService.getMonthlyCount(accountId)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 글쓰기 활동 통계 조회
     * - posts: 게시글 작성 수
     * - studyrooms: 스터디룸 개설 수
     * - comments: 댓글 작성 수
     * - total: 전체 합계 (posts + studyrooms + comments)
     */
    @GetMapping("/writing/count")
    public ResponseEntity<WritingCountResponse> getWritingCount(@CookieValue(name = "userToken", required = false) String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);

        long postCount = writingCountService.getPostsCount(accountId);
        long studyroomCount = writingCountService.getStudyroomsCount(accountId);
        long commentCount = writingCountService.getCommentsCount(accountId);

        WritingCountResponse response = new WritingCountResponse(
                postCount,
                studyroomCount,
                commentCount,
                postCount + studyroomCount + commentCount   // 총 작성 횟수
        );

        return ResponseEntity.ok(response);
    }
}