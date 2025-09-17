package com.wowraid.jobspoon.studyschedule.service.request;

import com.wowraid.jobspoon.studyschedule.entity.AttendanceStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateAttendanceRequest {
    private final Long studyMemberId;
    private final AttendanceStatus status;
}
