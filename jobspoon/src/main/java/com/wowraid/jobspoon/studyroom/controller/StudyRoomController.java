package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.RegisterStudyRoomResponseForm;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping
    public ResponseEntity<RegisterStudyRoomResponseForm> createStudyRoom(@RequestBody RegisterStudyRoomRequestForm requestForm) {

        RegisterStudyRoomResponse createdRegisterStudyRoomResponse = studyRoomService.createStudyRoom(requestForm);
        RegisterStudyRoomResponseForm responseForm = RegisterStudyRoomResponseForm.from(createdRegisterStudyRoomResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }
}