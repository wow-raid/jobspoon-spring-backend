package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomStatusRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.ListStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.UpdateStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping
    public ResponseEntity<Void> createStudyRoom(
            @RequestBody CreateStudyRoomRequestForm requestForm) {

        Long hostId = 1L;

        StudyRoom createdStudyRoom = studyRoomService.createStudyRoom(requestForm, hostId);

        // 생성된 리소스의 URI를 반환 (RESTful API 스타일)
        URI location = URI.create("/api/study-rooms/" + createdStudyRoom.getId());
        return ResponseEntity.created(location).build();
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
}