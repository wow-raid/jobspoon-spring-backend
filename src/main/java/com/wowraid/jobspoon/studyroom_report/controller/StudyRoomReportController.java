package com.wowraid.jobspoon.studyroom_report.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyroom_report.service.StudyRoomReportService;
import com.wowraid.jobspoon.studyroom_report.service.request.CreateStudyRoomReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/reports")
public class StudyRoomReportController {

    private final StudyRoomReportService studyRoomReportService;
    private final RedisCacheService redisCacheService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody CreateStudyRoomReportRequest request) {

        Long reporterId = redisCacheService.getValueByKey(userToken, Long.class);

        studyRoomReportService.createReport(request, reporterId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}