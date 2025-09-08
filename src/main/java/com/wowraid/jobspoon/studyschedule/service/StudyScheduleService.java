package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyschedule.service.request.CreateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.response.CreateStudyScheduleResponse;

import java.util.List;

public interface StudyScheduleService {
    CreateStudyScheduleResponse createSchedule(CreateStudyScheduleRequest request);

    List<CreateStudyScheduleResponse> findAllSchedules(Long studyRoomId);
}