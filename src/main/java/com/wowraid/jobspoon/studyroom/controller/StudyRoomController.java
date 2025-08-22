package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
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
}