package com.example.rooms.studyroom.controller;

import com.example.rooms.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.example.rooms.studyroom.controller.response_form.RegisterStudyRoomResponseForm;
import com.example.rooms.studyroom.service.StudyRoomService;
import com.example.rooms.studyroom.service.response.RegisterStudyRoomResponse;
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

    @PostMapping
    public ResponseEntity<RegisterStudyRoomResponseForm> createStudyRoom(@RequestBody RegisterStudyRoomRequestForm requestForm) {

        RegisterStudyRoomResponse createdRegisterStudyRoomResponse = studyRoomService.createStudyRoom(requestForm);
        RegisterStudyRoomResponseForm responseForm = RegisterStudyRoomResponseForm.from(createdRegisterStudyRoomResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseForm);
    }
}