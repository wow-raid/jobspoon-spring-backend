package com.wowraid.jobspoon.studyroom.controller.request_Form;

import com.wowraid.jobspoon.studyroom.service.request.CreateAnnouncementRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateAnnouncementRequestForm {
    private final String title;
    private final String content;

    public CreateAnnouncementRequest toServiceRequest(Long studyRoomId, Long authorId){
        return new CreateAnnouncementRequest(studyRoomId, authorId, title, content);
    }
}