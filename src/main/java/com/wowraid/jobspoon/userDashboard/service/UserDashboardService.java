package com.wowraid.jobspoon.userDashboard.service;

import com.wowraid.jobspoon.userDashboard.controller.response.UserDashboardSummaryResponse;

public interface UserDashboardService {
    UserDashboardSummaryResponse getMonthlySummary(Long accountId);
}
