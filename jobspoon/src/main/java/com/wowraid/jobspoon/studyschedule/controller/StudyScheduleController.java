package com.wowraid.jobspoon.studyschedule.controller;

import com.wowraid.jobspoon.studyschedule.controller.request_form.RegisterStudyScheduleRequestForm;
import com.wowraid.jobspoon.studyschedule.controller.response_form.RegisterStudyScheduleResponseForm;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import com.wowraid.jobspoon.studyschedule.service.StudyScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/{studyRoomId}/schedules")
public class StudyScheduleController {
    private final StudyScheduleService studyScheduleService;

    // 생성!!
    @PostMapping
    public ResponseEntity<Void> createStudySchedule(
            @PathVariable Long studyRoomId,
            @Valid @RequestBody RegisterStudyScheduleRequestForm request){

        Long scheduleId = studyScheduleService.createStudySchedule(studyRoomId, request);
        URI location = URI.create(String.format("/api/schedules/%d", scheduleId));
        return ResponseEntity.created(location).build();
    }

    // 스터디룸 내 스케줄 전체조회!!
    @GetMapping
    public ResponseEntity<List<RegisterStudyScheduleResponseForm>> findAllSchedules(@PathVariable Long studyRoomId){
        List<StudySchedule> schedules = studyScheduleService.findScheduleByStudyRoom(studyRoomId);
        List<RegisterStudyScheduleResponseForm> responseForm = schedules.stream()
                .map(RegisterStudyScheduleResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForm);
    }

    // 스터디룸 내 스케줄 특정조회!!
    @GetMapping("/{scheduleId}")
    public ResponseEntity<RegisterStudyScheduleResponseForm> findBySchedule(
            @PathVariable Long studyRoomId,
            @PathVariable Long scheduleId){

        StudySchedule schedule = studyScheduleService.findScheduleById(scheduleId);

        if ( !schedule.getStudyRoom().getId().equals(studyRoomId) ){
            return ResponseEntity.notFound().build();
        }

        RegisterStudyScheduleResponseForm responseForm = RegisterStudyScheduleResponseForm.from(schedule);
        return ResponseEntity.ok(responseForm);
    }
}
