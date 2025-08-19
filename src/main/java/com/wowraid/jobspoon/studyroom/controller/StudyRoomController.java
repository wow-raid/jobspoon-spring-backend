package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.RegisterStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;
import jakarta.validation.Valid;
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

    // 생성 컨트롤
    @PostMapping
    public ResponseEntity<RegisterStudyRoomResponseForm> createStudyRoom(@RequestBody RegisterStudyRoomRequestForm requestForm) {

        RegisterStudyRoomResponse createdRegisterStudyRoomResponse = studyRoomService.createStudyRoom(requestForm);
        RegisterStudyRoomResponseForm responseForm = RegisterStudyRoomResponseForm.from(createdRegisterStudyRoomResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }

    // 전체조회 컨트롤
    @GetMapping
    public ResponseEntity<List<RegisterStudyRoomResponseForm>> getAllStudyRooms() {
        List<RegisterStudyRoomResponse> responses = studyRoomService.findAllStudyRooms();
        List<RegisterStudyRoomResponseForm> responseForms = responses.stream()
                .map(RegisterStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }

    // 필터조회(지역) 컨트롤
    @GetMapping(params = "region")
    public ResponseEntity<List<RegisterStudyRoomResponseForm>> getStudyRoomsByRegion(@RequestParam String region) {
        List<RegisterStudyRoomResponse> responses = studyRoomService.findStudyRoomsByRegion(region);
        List<RegisterStudyRoomResponseForm> responseForms = responses.stream()
                .map(RegisterStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }

    // 수정 컨트롤
    @PutMapping("/update/{studyRoomId}")
    public ResponseEntity<RegisterStudyRoomResponseForm> updateStudyRoom(
            @PathVariable Long studyRoomId,
            @Valid @RequestBody UpdateStudyRoomRequestForm requestForm) {
        RegisterStudyRoomResponse updateRegisterStudyRoomResponse = studyRoomService.updateStudyRoom(studyRoomId, requestForm);
        RegisterStudyRoomResponseForm responseForm = RegisterStudyRoomResponseForm.from(updateRegisterStudyRoomResponse);
        return ResponseEntity.ok(responseForm);
    }

    // 삭제 컨트롤
    @DeleteMapping("/delete/{studyRoomId}")
    public ResponseEntity<Void>  deleteStudyRoom(@PathVariable Long studyRoomId) {
        studyRoomService.deleteStudyRoom(studyRoomId);

        return ResponseEntity.noContent().build();
    }
}