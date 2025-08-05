package com.wowraid.jobspoon.studyschedule.controller.response_form;

import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RegisterStudyScheduleResponseForm {
    private final Long id;
    private final String title;
    private final String content;
    private final String place;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public RegisterStudyScheduleResponseForm(StudySchedule studySchedule) {
        this.id = studySchedule.getId();
        this.title = studySchedule.getTitle();
        this.content = studySchedule.getContent();
        this.place = studySchedule.getPlace();
        this.startTime = studySchedule.getStartTime();
        this.endTime = studySchedule.getEndTime();
    }

    public static RegisterStudyScheduleResponseForm from(StudySchedule studySchedule) {
        return new RegisterStudyScheduleResponseForm(studySchedule);
    }
}
