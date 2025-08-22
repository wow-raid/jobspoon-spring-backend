package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.CreateStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
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
    public ResponseEntity<CreateStudyRoomResponseForm> createStudyRoom(@RequestBody CreateStudyRoomRequestForm requestForm) {

        CreateStudyRoomResponse createdCreateStudyRoomResponse = studyRoomService.createStudyRoom(requestForm);
        CreateStudyRoomResponseForm responseForm = CreateStudyRoomResponseForm.from(createdCreateStudyRoomResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }

    // 전체조회 컨트롤
    @GetMapping
    public ResponseEntity<List<CreateStudyRoomResponseForm>> getAllStudyRooms() {
        List<CreateStudyRoomResponse> responses = studyRoomService.findAllStudyRooms();
        List<CreateStudyRoomResponseForm> responseForms = responses.stream()
                .map(CreateStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }

    // 필터조회(지역) 컨트롤
    @GetMapping(params = "region")
    public ResponseEntity<List<CreateStudyRoomResponseForm>> getStudyRoomsByRegion(@RequestParam String region) {
        List<CreateStudyRoomResponse> responses = studyRoomService.findStudyRoomsByRegion(region);
        List<CreateStudyRoomResponseForm> responseForms = responses.stream()
                .map(CreateStudyRoomResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseForms);
    }

    // 수정 컨트롤
    @PutMapping("/update/{studyRoomId}")
    public ResponseEntity<CreateStudyRoomResponseForm> updateStudyRoom(
            @PathVariable Long studyRoomId,
            @Valid @RequestBody UpdateStudyRoomRequestForm requestForm) {
        CreateStudyRoomResponse updateCreateStudyRoomResponse = studyRoomService.updateStudyRoom(studyRoomId, requestForm);
        CreateStudyRoomResponseForm responseForm = CreateStudyRoomResponseForm.from(updateCreateStudyRoomResponse);
        return ResponseEntity.ok(responseForm);
    }

    // 삭제 컨트롤
    @DeleteMapping("/delete/{studyRoomId}")
    public ResponseEntity<Void>  deleteStudyRoom(@PathVariable Long studyRoomId) {
        studyRoomService.deleteStudyRoom(studyRoomId);

        return ResponseEntity.noContent().build();
    }
}