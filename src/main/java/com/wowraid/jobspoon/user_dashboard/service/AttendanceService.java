package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.AttendanceRateResponse;

public interface AttendanceService {
    boolean markLogin(Long accountId);
    AttendanceRateResponse getThisMonthRate(Long accountId);
}
