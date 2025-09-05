package com.wowraid.jobspoon.studyroom.controller.request_Form;

import com.wowraid.jobspoon.studyroom.service.request.UpdateAnnouncementRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAnnouncementRequestForm {
    private final String title;
    private final String content;

    public UpdateAnnouncementRequest toServiceRequest(){
        return new UpdateAnnouncementRequest(this.title,this.content);
    }
}