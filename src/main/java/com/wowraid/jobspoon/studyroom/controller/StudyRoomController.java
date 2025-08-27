package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomStatusRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.CreateStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.ListStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.ReadStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.UpdateStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.ReadStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping
    public ResponseEntity<CreateStudyRoomResponseForm> createStudyRoom(
            @RequestBody CreateStudyRoomRequestForm requestForm) {

        Long hostId = 1L;

        // 👇 Service는 이제 CreateStudyRoomResponse를 반환합니다.
        CreateStudyRoomResponse serviceResponse = studyRoomService.createStudyRoom(requestForm, hostId);

        // 👇 Service 응답을 Controller Form으로 변환합니다.
        CreateStudyRoomResponseForm responseForm = CreateStudyRoomResponseForm.from(serviceResponse);

        // 👇 생성된 데이터를 Body에 담아 201 Created 응답을 보냅니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }

    @GetMapping
    public ResponseEntity<ListStudyRoomResponseForm> getAllStudyRooms(
            @RequestParam(required = false) Long lastStudyId,
            @RequestParam int size) {

        ListStudyRoomRequest serviceRequest = new ListStudyRoomRequest(lastStudyId, size);
        ListStudyRoomResponse serviceResponse = studyRoomService.findAllStudyRooms(serviceRequest);
        ListStudyRoomResponseForm responseForm = ListStudyRoomResponseForm.from(serviceResponse);

        return ResponseEntity.ok(responseForm);
    }

    @GetMapping("/{studyRoomId}")
    public ResponseEntity<ReadStudyRoomResponseForm> readStudyRoom(@PathVariable Long studyRoomId) {

        ReadStudyRoomResponse serviceResponse = studyRoomService.readStudyRoom(studyRoomId);
        return ResponseEntity.ok(ReadStudyRoomResponseForm.from(serviceResponse));
    }

    @PutMapping("/{studyRoomId}")
    public ResponseEntity<UpdateStudyRoomResponseForm> updateStudyRoom (
            @PathVariable Long studyRoomId,
            @RequestBody UpdateStudyRoomRequestForm requestForm) {

        // Controller는 RequestForm을 Service용 Request 객체로 변환
        UpdateStudyRoomResponse serviceResponse = studyRoomService.updateStudyRoom(studyRoomId, requestForm.toServiceRequest());

        // Service에서 받은 Response를 Controller용 Form 객체로 변환
        UpdateStudyRoomResponseForm responseForm = UpdateStudyRoomResponseForm.from(serviceResponse);

        return ResponseEntity.ok(responseForm);
    }

    @PatchMapping("/{studyRoomId}/status")
    public ResponseEntity<Void> updateStudyRoomStatus(
            @PathVariable Long studyRoomId,
            @RequestBody UpdateStudyRoomStatusRequestForm requestForm) {
        studyRoomService.updateStudyRoomStatus(studyRoomId, requestForm.toServiceRequest());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studyRoomId}")
    public ResponseEntity<Void> deleteStudyRoom(@PathVariable Long studyRoomId) {
        Long currentUserId = 1L;        // 추후에 실제 로그인 Id를 가져와야함.

        studyRoomService.deleteStudyRoom(studyRoomId, currentUserId);

        return ResponseEntity.noContent().build();
    }
}