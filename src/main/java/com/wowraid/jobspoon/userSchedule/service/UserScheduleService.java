package com.wowraid.jobspoon.userSchedule.service;

import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;

public interface UserScheduleService {
    UserSchedule createUserSchedule(Long accountId, UserScheduleRequest request);
}
