package com.wowraid.jobspoon.userSchedule.controller;

import com.wowraid.jobspoon.userDashboard.service.TokenAccountService;
import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.controller.response.UserScheduleResponse;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;
import com.wowraid.jobspoon.userSchedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-schedule")
public class UserScheduleController {

    private final UserScheduleService userScheduleService;
    private final TokenAccountService tokenAccountService;

    // 일정 등록
    @PostMapping("/create")
    public ResponseEntity<UserScheduleResponse> createUserSchedule(
            @CookieValue(name = "userToken") String userToken,
            @RequestBody UserScheduleRequest request
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        UserSchedule created = userScheduleService.createUserSchedule(accountId, request);
        return ResponseEntity.ok(new UserScheduleResponse(created));
    }
}
