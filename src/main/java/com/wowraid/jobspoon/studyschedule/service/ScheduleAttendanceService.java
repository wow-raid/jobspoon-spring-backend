package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyschedule.service.request.UpdateAttendanceRequest;
import com.wowraid.jobspoon.studyschedule.service.response.CreateScheduleAttendanceResponse;
import com.wowraid.jobspoon.studyschedule.service.response.ListAttendanceStatusResponse;

import java.util.List;

public interface ScheduleAttendanceService {

    CreateScheduleAttendanceResponse checkAttendance(Long studyScheduleId, Long accountProfileId);

    List<ListAttendanceStatusResponse> getAttendanceList(Long studyScheduleId, Long leaderId);

    void confirmAttendance(Long studyScheduleId, Long leaderId, List<UpdateAttendanceRequest> requests);

}