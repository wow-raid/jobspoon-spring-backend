package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.service.request.CreateAnnouncementRequest;
import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;

public interface AnnouncementService {
    CreateAnnouncementResponse createAnnouncement(CreateAnnouncementRequest request);
}