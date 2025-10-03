package com.wowraid.jobspoon.report.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.report.service.ReportService;
import com.wowraid.jobspoon.report.service.request.CreateReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/reports")
public class StudyRoomReportController {

    private final ReportService reportService;
    private final RedisCacheService redisCacheService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody CreateReportRequest request) {

        Long reporterId = redisCacheService.getValueByKey(userToken, Long.class);

        reportService.createReport(request, reporterId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}