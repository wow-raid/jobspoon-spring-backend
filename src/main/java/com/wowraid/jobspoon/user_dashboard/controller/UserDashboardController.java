package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityResponse;
import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import com.wowraid.jobspoon.user_dashboard.service.UserDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-dashboard")
public class UserDashboardController {

    private final UserDashboardService dashboardService;
    private final TokenAccountService tokenAccountService;

    @GetMapping("/my")
    public ResponseEntity<ActivityResponse> getDashboard(@RequestHeader("Authorization") String userToken) {
        //token으로 accountId 조회
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(dashboardService.getDashboardByAccountId(accountId));
    }
}
