package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyApplication.service.StudyApplicationService;
import com.wowraid.jobspoon.studyApplication.service.response.MyApplicationStatusResponse;
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
    private final StudyApplicationService studyApplicationService;

    @PostMapping
    public ResponseEntity<CreateStudyRoomResponseForm> createStudyRoom(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody CreateStudyRoomRequestForm requestForm) {

        Long hostId = redisCacheService.getValueByKey(userToken, Long.class);

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
    public ResponseEntity<ReadStudyRoomResponseForm> readStudyRoom(
            @PathVariable Long studyRoomId,
            // ✅ 쿠키에서 토큰을 가져옵니다.
            @CookieValue(name = "userToken", required = false) String userToken) {

        Long currentUserId = null;
        if (userToken != null) {
            // ✅ Redis에서 사용자 ID를 조회합니다.
            currentUserId = redisCacheService.getValueByKey(userToken, Long.class);
        }

        // ✅ 조회한 사용자 ID를 서비스에 전달합니다.
        ReadStudyRoomResponse serviceResponse = studyRoomService.readStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.ok(ReadStudyRoomResponseForm.from(serviceResponse));
    }

    @GetMapping("/my-studies")
    public ResponseEntity<List<MyStudyResponse>> getMyStudies(
            @CookieValue(name = "userToken", required = false) String userToken) {

        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        List<MyStudyResponse> myStudiesResponse = studyRoomService.findMyStudies(currentUserId);

        return ResponseEntity.ok(myStudiesResponse);
    }

    @GetMapping("/{studyRoomId}/members")
    public ResponseEntity<List<StudyMemberResponseForm>> getStudyMembers(
            @PathVariable Long studyRoomId) {
        List<StudyMemberResponse> serviceResponse = studyRoomService.getStudyMembers(studyRoomId);
        List<StudyMemberResponseForm> response = serviceResponse.stream()
                .map(StudyMemberResponseForm::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{studyRoomId}/role")
    public ResponseEntity<String> getUserRoleInStudyRoom(
            @PathVariable Long studyRoomId,
            @CookieValue(name = "userToken", required = false) String userToken) {

        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        String role = studyRoomService.findUserRoleInStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{studyRoomId}")
    public ResponseEntity<UpdateStudyRoomResponseForm> updateStudyRoom(
            @PathVariable Long studyRoomId,
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody UpdateStudyRoomRequestForm requestForm) {

        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        UpdateStudyRoomResponse serviceResponse = studyRoomService.updateStudyRoom(
                studyRoomId, currentUserId, requestForm.toServiceRequest()
        );
        return ResponseEntity.ok(UpdateStudyRoomResponseForm.from(serviceResponse));
    }

    @PatchMapping("/{studyRoomId}/status")
    public ResponseEntity<Void> updateStudyRoomStatus(
            @PathVariable Long studyRoomId,
            @CookieValue(value = "userToken", required = false) String userToken,
            @RequestBody UpdateStudyRoomStatusRequestForm requestForm) {

        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        studyRoomService.updateStudyRoomStatus(studyRoomId, currentUserId, requestForm.toServiceRequest());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{studyRoomId}")
    public ResponseEntity<Void> deleteStudyRoom(
            @PathVariable Long studyRoomId,
            @CookieValue(name = "userToken", required = false) String userToken) {

        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        studyRoomService.deleteStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // 스터디에서 참가자가 탈퇴하는 API
    @DeleteMapping("/{studyRoomId}/membership")
    public ResponseEntity<Void> leaveStudyRoom(
            @PathVariable Long studyRoomId,
            @CookieValue(name = "userToken", required = false) String userToken) {
        Long currentUserId = redisCacheService.getValueByKey(userToken, Long.class);

        studyRoomService.leaveStudyRoom(studyRoomId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // 스터디에서 멤버를 강퇴시키는 API
    @DeleteMapping("/{studyRoomId}/members/{memberId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable Long studyRoomId,
            @PathVariable Long memberId,
            @CookieValue(name = "userToken", required = false) String userToken) {

        Long leaderId = redisCacheService.getValueByKey(userToken, Long.class);

        studyRoomService.kickMember(studyRoomId, memberId, leaderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{studyRoomId}/my-application")
    public ResponseEntity<MyApplicationStatusResponse> getMyApplicationStatus(
            @PathVariable Long studyRoomId,
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long applicantId = null;
        if (userToken != null) {
            applicantId = redisCacheService.getValueByKey(userToken, Long.class);
        }
        MyApplicationStatusResponse response = studyApplicationService.findMyApplicationStatus(studyRoomId, applicantId);
        return ResponseEntity.ok(response);
    }

    // 모의면접 채널 조회 API
    @GetMapping("/{studyRoomId}/interview-channels")
    public ResponseEntity<List<InterviewChannelResponse>> getInterviewChannels(
            @PathVariable Long studyRoomId) {
        List<InterviewChannelResponse> channels = studyRoomService.findInterviewChannels(studyRoomId);
        return ResponseEntity.ok(channels);
    }
}