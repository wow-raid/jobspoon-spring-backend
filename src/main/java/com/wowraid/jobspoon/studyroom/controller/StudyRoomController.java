package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyroom.controller.request_Form.*;
import com.wowraid.jobspoon.studyroom.controller.response_form.*;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    @GetMapping("/my-studies")
    public ResponseEntity<List<ReadStudyRoomResponseForm>> getMyStudies(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        // 이제 서비스는 List<StudyRoom>을 반환합니다.
        List<StudyRoom> myStudies = studyRoomService.findMyStudies(currentUserId);

        // 새로 만든 from(StudyRoom) 메소드를 사용하여 한 번에 변환합니다.
        List<ReadStudyRoomResponseForm> response = myStudies.stream()
                .map(ReadStudyRoomResponseForm::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 스터디모임 내 '참여인원' 탭 API
    @GetMapping("/{studyRoomId}/members")
    public ResponseEntity<List<StudyMemberResponseForm>> getStudyMembers(@PathVariable Long studyRoomId) {
        List<StudyMemberResponse> serviceResponse = studyRoomService.getStudyMembers(studyRoomId);
        List<StudyMemberResponseForm> response = serviceResponse.stream()
                .map(StudyMemberResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
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

    // 스터디에서 참가자가 탈퇴하는 API
    @DeleteMapping("/{studyRoomId}/membership")
    public ResponseEntity<Void> leaveStudyRoom(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Long currentUserId = redisCacheService.getValueByKey(token, Long.class);

        studyRoomService.leaveStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}