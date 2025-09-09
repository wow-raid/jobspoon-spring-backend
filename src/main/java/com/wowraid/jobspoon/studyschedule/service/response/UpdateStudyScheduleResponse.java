package com.wowraid.jobspoon.studyschedule.service.response;

import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UpdateStudyScheduleResponse {
    private final Long id;
    private final Long authorId;
    private final String authorNickname;
    private final String title;
    private final String description;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public static UpdateStudyScheduleResponse from(StudySchedule schedule) {
        return new UpdateStudyScheduleResponse(
                schedule.getId(),
                schedule.getAuthor().getId(),
                schedule.getAuthor().getNickname(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
    }
}