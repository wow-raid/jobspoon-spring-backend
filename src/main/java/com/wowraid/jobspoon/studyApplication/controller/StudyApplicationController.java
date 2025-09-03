package com.wowraid.jobspoon.studyApplication.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyApplication.controller.request_form.CreateStudyApplicationRequestForm;
import com.wowraid.jobspoon.studyApplication.controller.response_form.CreateStudyApplicationResponseForm;
import com.wowraid.jobspoon.studyApplication.service.StudyApplicationService;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-applications")
public class StudyApplicationController {

    private final StudyApplicationService studyApplicationService;
    private final RedisCacheService redisCacheService;

    @PostMapping
    public ResponseEntity<CreateStudyApplicationResponseForm> applyToStudy(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateStudyApplicationRequestForm requestForm
    ) {
        log.info("스터디 지원 요청 시작. studyRoomId={}, message='{}'", requestForm.getStudyRoomId(), requestForm.getMessage());

        // 'Bearer ' 형식 검증 (유지)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
        }
        String token = authorizationHeader.substring(7);

        // Redis 조회 및 null 체크 (유지)
        Long applicantId = redisCacheService.getValueByKey(token, Long.class);
        if (applicantId == null) {
            log.warn("유효하지 않은 토큰으로 인한 접근 거부. token={}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CreateStudyApplicationResponse serviceResponse =
                studyApplicationService.applyToStudy(requestForm.toServiceRequest(applicantId));

        CreateStudyApplicationResponseForm responseForm = CreateStudyApplicationResponseForm.from(serviceResponse);

        log.info("스터디 지원 성공. applicationId={}", responseForm.getApplicationId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/study-applications/" + responseForm.getApplicationId()))
                .body(responseForm);
    }
}