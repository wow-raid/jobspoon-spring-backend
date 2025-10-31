package com.wowraid.jobspoon.userAttendance.service;

import com.wowraid.jobspoon.userDashboard.controller.response.AttendanceRateResponse;

public interface AttendanceService {
    boolean markLogin(Long accountId);
    AttendanceRateResponse getThisMonthRate(Long accountId);
    int getConsecutiveDays(Long accountId);
}
