package com.wowraid.jobspoon.user_dashboard.service;

public interface AttendanceService {
    void markLogin(Long accountId);
    double getThisMonthRate(Long accountId);
}
