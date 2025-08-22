package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.ListStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
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

        StudyRoom createdStudyRoom = studyRoomService.createStudyRoom(requestForm);

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
}