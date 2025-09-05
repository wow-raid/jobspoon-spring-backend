package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateAnnouncementResponseForm {
    private final Long id;
    private final String title;
    private final LocalDateTime createdAt;

    public static CreateAnnouncementResponseForm from(CreateAnnouncementResponse response){
        return new CreateAnnouncementResponseForm(
                response.getId(),
                response.getTitle(),
                response.getCreatedAt()
        );
    }
}