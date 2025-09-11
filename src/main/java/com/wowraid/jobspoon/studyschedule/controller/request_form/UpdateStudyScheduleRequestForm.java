package com.wowraid.jobspoon.studyschedule.controller.request_form;

import com.wowraid.jobspoon.studyschedule.service.request.UpdateStudyScheduleRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UpdateStudyScheduleRequestForm {
    private final String title;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public UpdateStudyScheduleRequest toServiceRequest() {
        return new UpdateStudyScheduleRequest(this.title, this.description, this.startTime, this.endTime);
    }
}