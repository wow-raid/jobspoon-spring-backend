package com.wowraid.jobspoon.studyroom.service.response;

import com.wowraid.jobspoon.studyroom.entity.Announcement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateAnnouncementResponse {
    private final Long id;
    private final String title;
    private final LocalDateTime createdAt;

    public static CreateAnnouncementResponse from(Announcement announcement) {
        return new  CreateAnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getCreatedAt()
        );
    }
}