package com.wowraid.jobspoon.studyschedule.service.response;

import com.wowraid.jobspoon.studyschedule.entity.AttendanceStatus;
import com.wowraid.jobspoon.studyschedule.entity.ScheduleAttendance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CreateScheduleAttendanceResponse {
    private final Long attendanceId;
    private final Long studyScheduleId;
    private final Long studyMemberId;
    private final AttendanceStatus status;

    private CreateScheduleAttendanceResponse(ScheduleAttendance attendance) {
        this.attendanceId = attendance.getId();
        this.studyScheduleId = attendance.getStudySchedule().getId();
        this.studyMemberId = attendance.getStudyMember().getId();
        this.status = attendance.getStatus();
    }

    public static CreateScheduleAttendanceResponse from(ScheduleAttendance attendance) {
        return new CreateScheduleAttendanceResponse(attendance);
    }
}