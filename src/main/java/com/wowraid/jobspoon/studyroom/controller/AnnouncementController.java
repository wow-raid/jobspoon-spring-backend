package com.wowraid.jobspoon.studyroom.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateAnnouncementRequestForm;
import com.wowraid.jobspoon.studyroom.controller.response_form.CreateAnnouncementResponseForm;
import com.wowraid.jobspoon.studyroom.service.AnnouncementService;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study-rooms/{studyRoomId}/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final RedisCacheService redisCacheService;
    private final StudyRoomService studyRoomService;

    @PostMapping
    public ResponseEntity<CreateAnnouncementResponseForm> createAnnouncement(
            @PathVariable Long studyRoomId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateAnnouncementRequestForm requestForm) {

        String token = authorizationHeader.substring(7);
        Long authorId = redisCacheService.getValueByKey(token, Long.class);

        String role = studyRoomService.findUserRoleInStudyRoom(studyRoomId, authorId);
        if (!"LEADER".equals(role)) {
            throw new IllegalStateException("리더만 공지사항을 작성할 수 있습니다.");
        }

        CreateAnnouncementResponse serviceResponse = announcementService.createAnnouncement(
                requestForm.toServiceRequest(studyRoomId, authorId)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateAnnouncementResponseForm.from(serviceResponse));
    }
}