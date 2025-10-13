package com.wowraid.jobspoon.userSchedule.controller;

import com.wowraid.jobspoon.userDashboard.service.TokenAccountService;
import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.controller.response.UserScheduleResponse;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;
import com.wowraid.jobspoon.userSchedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 전체 일정 조회
    @GetMapping("/list")
    public ResponseEntity<List<UserScheduleResponse>> getListUserSchedules(
            @CookieValue(name = "userToken") String userToken
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        List<UserScheduleResponse> responses = userScheduleService.getUserSchedules(accountId)
                .stream()
                .map(UserScheduleResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 특정 일정 상세 보기
    @GetMapping("/{id}")
    public ResponseEntity<UserScheduleResponse> getUserScheduleDetail(
            @CookieValue(name = "userToken") String userToken,
            @PathVariable Long id
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        UserSchedule schedule = userScheduleService.getUserScheduleById(accountId, id);
        return ResponseEntity.ok(new UserScheduleResponse(schedule));
    }
}
