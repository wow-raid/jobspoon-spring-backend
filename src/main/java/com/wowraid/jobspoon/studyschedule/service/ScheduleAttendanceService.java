package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyschedule.service.response.CreateScheduleAttendanceResponse;

public interface ScheduleAttendanceService {

    CreateScheduleAttendanceResponse checkAttendance(Long studyScheduleId, Long accountProfileId);

}