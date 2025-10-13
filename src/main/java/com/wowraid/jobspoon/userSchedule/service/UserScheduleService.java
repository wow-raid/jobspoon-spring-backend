package com.wowraid.jobspoon.userSchedule.service;

import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;

import java.util.List;

public interface UserScheduleService {
    UserSchedule createUserSchedule(Long accountId, UserScheduleRequest request);
    List<UserSchedule> getUserSchedules(Long accountId);
    UserSchedule getUserScheduleById(Long accountId, Long id);
}
