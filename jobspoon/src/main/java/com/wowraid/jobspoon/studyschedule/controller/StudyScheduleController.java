package com.wowraid.jobspoon.studyschedule.controller;

import com.wowraid.jobspoon.studyschedule.controller.request_form.RegisterStudyScheduleRequestForm;
import com.wowraid.jobspoon.studyschedule.service.StudyScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/{studyRoomId}/schedules")
public class StudyScheduleController {
    private final StudyScheduleService studyScheduleService;

    @PostMapping
    public ResponseEntity<Void> createStudySchedule(
            @PathVariable Long studyRoomId,
            @Valid @RequestBody RegisterStudyScheduleRequestForm request){

        Long scheduleId = studyScheduleService.createStudySchedule(studyRoomId, request);
        URI location = URI.create(String.format("/api/schedules/%d", scheduleId));
        return ResponseEntity.created(location).build();
    }
}
