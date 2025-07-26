package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.RegisterStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    // 이건 생성이다옹
    @PostMapping
    public ResponseEntity<RegisterStudyRoomResponseForm> createStudyRoom(@RequestBody RegisterStudyRoomRequestForm requestForm) {

        RegisterStudyRoomResponse createdRegisterStudyRoomResponse = studyRoomService.createStudyRoom(requestForm);
        RegisterStudyRoomResponseForm responseForm = RegisterStudyRoomResponseForm.from(createdRegisterStudyRoomResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }

    // 이건 전체조회다옹
    @GetMapping
    public ResponseEntity<List<RegisterStudyRoomResponseForm>> getAllStudyRooms() {
        List<RegisterStudyRoomResponse> responses = studyRoomService.findAllStudyRooms();
        List<RegisterStudyRoomResponseForm> responseForms = responses.stream()
                .map(RegisterStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }

    // 이건 지역별 조회다옹!!
    @GetMapping(params = "region")
    public ResponseEntity<List<RegisterStudyRoomResponseForm>> getStudyRoomsByRegion(@RequestParam String region) {
        List<RegisterStudyRoomResponse> responses = studyRoomService.findStudyRoomsByRegion(region);
        List<RegisterStudyRoomResponseForm> responseForms = responses.stream()
                .map(RegisterStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }
}