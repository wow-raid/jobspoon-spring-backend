package com.wowraid.jobspoon.user_dashboard.controller;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.AttendanceRateResponse;
import com.wowraid.jobspoon.user_dashboard.service.AttendanceService;
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

    @GetMapping("/attendance/rate")
    public ResponseEntity<AttendanceRateResponse> getRete(@RequestHeader("Authorization") String userToken){

        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AttendanceRateResponse response = attendanceService.getThisMonthRate(accountId);

        return ResponseEntity.ok(response);
    }
}
