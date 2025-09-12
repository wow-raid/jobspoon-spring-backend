package com.wowraid.jobspoon.studyApplication.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyApplication.controller.request_form.CreateStudyApplicationRequestForm;
import com.wowraid.jobspoon.studyApplication.controller.response_form.CreateStudyApplicationResponseForm;
import com.wowraid.jobspoon.studyApplication.controller.response_form.ListMyApplicationResponseForm;
import com.wowraid.jobspoon.studyApplication.service.StudyApplicationService;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import com.wowraid.jobspoon.studyApplication.service.response.ListMyApplicationResponse;
import com.wowraid.jobspoon.studyApplication.service.response.MyApplicationStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

        // 'Bearer ' 형식 검증 (유지)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
        }
        String token = authorizationHeader.substring(7);

        // Redis 조회 및 null 체크 (유지)
        Long applicantId = redisCacheService.getValueByKey(token, Long.class);
        if (applicantId == null) {
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

    @GetMapping("/my")
    public ResponseEntity<List<ListMyApplicationResponseForm>> getMyApplications(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Long applicantId = redisCacheService.getValueByKey(token, Long.class);

        List<ListMyApplicationResponse> serviceResponse = studyApplicationService.findMyApplications(applicantId);

        // 서비스 DTO를 컨트롤러 Form으로 변환 (프론트엔드와 최종 약속)
        List<ListMyApplicationResponseForm> response = serviceResponse.stream()
                .map(ListMyApplicationResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> cancelApplication(
            @PathVariable Long applicationId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Long applicantId = redisCacheService.getValueByKey(token, Long.class);

        studyApplicationService.cancelApplication(applicationId, applicantId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/study-rooms/{studyRoomId}/my-application")
    public ResponseEntity<MyApplicationStatusResponse> getMyApplicationStatus(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        Long applicantId = redisCacheService.getValueByKey(token, Long.class);

        MyApplicationStatusResponse response = studyApplicationService.findMyApplicationStatus(studyRoomId, applicantId);
        return ResponseEntity.ok(response);
    }
}