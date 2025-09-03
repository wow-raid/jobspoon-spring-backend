package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyroom.controller.request_Form.*;
import com.wowraid.jobspoon.studyroom.controller.response_form.*;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.*;
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
    private final RedisCacheService redisCacheService;

    @PostMapping
    public ResponseEntity<CreateStudyRoomResponseForm> createStudyRoom(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateStudyRoomRequestForm requestForm) {

        String token = authorizationHeader.substring(7);
        Long hostId = redisCacheService.getValueByKey(token, Long.class);

        CreateStudyRoomResponse serviceResponse = studyRoomService.createStudyRoom(requestForm.toServiceRequest(hostId));
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateStudyRoomResponseForm.from(serviceResponse));
    }

    @GetMapping
    public ResponseEntity<ListStudyRoomResponseForm> getAllStudyRooms(
            @RequestParam(required = false) Long lastStudyId,
            @RequestParam int size) {
        ListStudyRoomRequest serviceRequest = new ListStudyRoomRequest(lastStudyId, size);
        ListStudyRoomResponse serviceResponse = studyRoomService.findAllStudyRooms(serviceRequest);
        return ResponseEntity.ok(ListStudyRoomResponseForm.from(serviceResponse));
    }

    @GetMapping("/{studyRoomId}")
    public ResponseEntity<ReadStudyRoomResponseForm> readStudyRoom(@PathVariable Long studyRoomId) {
        ReadStudyRoomResponse serviceResponse = studyRoomService.readStudyRoom(studyRoomId);
        return ResponseEntity.ok(ReadStudyRoomResponseForm.from(serviceResponse));
    }

    @PutMapping("/{studyRoomId}")
    public ResponseEntity<UpdateStudyRoomResponseForm> updateStudyRoom(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateStudyRoomRequestForm requestForm) {

        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        UpdateStudyRoomResponse serviceResponse = studyRoomService.updateStudyRoom(
                studyRoomId, currentUserId, requestForm.toServiceRequest()
        );
        return ResponseEntity.ok(UpdateStudyRoomResponseForm.from(serviceResponse));
    }

    @PatchMapping("/{studyRoomId}/status")
    public ResponseEntity<Void> updateStudyRoomStatus(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UpdateStudyRoomStatusRequestForm requestForm) {

        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        studyRoomService.updateStudyRoomStatus(studyRoomId, currentUserId, requestForm.toServiceRequest());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studyRoomId}")
    public ResponseEntity<Void> deleteStudyRoom(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        studyRoomService.deleteStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}