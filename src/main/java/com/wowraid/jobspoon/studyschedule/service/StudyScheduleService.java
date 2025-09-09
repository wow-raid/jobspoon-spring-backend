package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyschedule.service.request.CreateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.response.CreateStudyScheduleResponse;
import com.wowraid.jobspoon.studyschedule.service.response.ListStudyScheduleResponse;
import com.wowraid.jobspoon.studyschedule.service.response.ReadStudyScheduleResponse;

import java.util.List;

public interface StudyScheduleService {
    CreateStudyScheduleResponse createSchedule(CreateStudyScheduleRequest request);

    List<ListStudyScheduleResponse> findAllSchedules(Long studyRoomId);

    ReadStudyScheduleResponse findScheduleById(Long scheduleId);
}