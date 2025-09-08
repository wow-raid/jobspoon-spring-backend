package com.wowraid.jobspoon.studyschedule.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyschedule.controller.request_form.CreateStudyScheduleRequestForm;
import com.wowraid.jobspoon.studyschedule.controller.response_form.CreateStudyScheduleResponseForm;
import com.wowraid.jobspoon.studyschedule.service.StudyScheduleService;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyschedule.service.response.CreateStudyScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/{studyRoomId}/schedules")
public class StudyScheduleController {
    private final StudyScheduleService studyScheduleService;
    private final StudyRoomService studyRoomService;
    private final RedisCacheService redisCacheService;

    @PostMapping
    public ResponseEntity<CreateStudyScheduleResponseForm> createSchedule(
        @PathVariable Long studyRoomId,
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody CreateStudyScheduleRequestForm requestForm) {

        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        studyRoomService.findUserRoleInStudyRoom(studyRoomId, currentUserId);

        var serviceRequest = requestForm.toServiceRequest(studyRoomId, currentUserId);
        CreateStudyScheduleResponse serviceResponse = studyScheduleService.createSchedule(serviceRequest);
        CreateStudyScheduleResponseForm responseForm = CreateStudyScheduleResponseForm.from(serviceResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }

    // 특정 스터디룸의 모든 일정 조회 API
    @GetMapping
    public ResponseEntity<List<CreateStudyScheduleResponseForm>> getAllSchedules(
        @PathVariable Long studyRoomId) {

        List<CreateStudyScheduleResponse> serviceResponse = studyScheduleService.findAllSchedules(studyRoomId);
        List<CreateStudyScheduleResponseForm> responseForms = serviceResponse.stream()
                .map(CreateStudyScheduleResponseForm::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseForms);
    }
}