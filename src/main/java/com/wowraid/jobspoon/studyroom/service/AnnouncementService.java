package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.service.request.CreateAnnouncementRequest;
import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;
import com.wowraid.jobspoon.studyroom.service.response.ListAnnouncementResponse;
import com.wowraid.jobspoon.studyroom.service.response.ReadAnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    CreateAnnouncementResponse createAnnouncement(CreateAnnouncementRequest request);

    List<ListAnnouncementResponse> findAllAnnouncements(Long studyRoomId);

    void toggleAnnouncementPin(Long studyRoomId, Long announcementId);

    ReadAnnouncementResponse findAnnouncementById(Long announcementId);
}