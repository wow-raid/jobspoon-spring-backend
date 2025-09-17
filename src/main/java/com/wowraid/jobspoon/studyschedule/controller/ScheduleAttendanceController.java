package com.wowraid.jobspoon.studyschedule.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyschedule.service.ScheduleAttendanceService;
import com.wowraid.jobspoon.studyschedule.service.response.CreateScheduleAttendanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules/{scheduleId}/attendance")
public class ScheduleAttendanceController {

    private final ScheduleAttendanceService scheduleAttendanceService;
    private final RedisCacheService redisCacheService;

    // 멤버가 특정 일정에 출석 체크
    @PostMapping
    public ResponseEntity<CreateScheduleAttendanceResponse> checkAttendance(
            @PathVariable Long scheduleId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long accountProfileId = redisCacheService.getValueByKey(token, Long.class);

        CreateScheduleAttendanceResponse response = scheduleAttendanceService.checkAttendance(scheduleId, accountProfileId);

        // 생성(Create)의 의미로 201 Created 상태 코드 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}