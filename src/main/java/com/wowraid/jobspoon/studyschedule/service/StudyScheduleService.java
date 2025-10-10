package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyschedule.service.request.CreateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.request.UpdateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.response.*;

import java.util.List;

public interface StudyScheduleService {
    CreateStudyScheduleResponse createSchedule(CreateStudyScheduleRequest request);

    List<ListStudyScheduleResponse> findAllSchedules(Long studyRoomId);

    ReadStudyScheduleResponse findScheduleById(Long scheduleId);

    UpdateStudyScheduleResponse updateSchedule(Long scheduleId, Long currentUserId, UpdateStudyScheduleRequest request);

    void deleteSchedule(Long scheduleId, Long currentUserId);

    List<ListUserStudyScheduleResponse> findAllSchedulesByUser(Long accountId);
}