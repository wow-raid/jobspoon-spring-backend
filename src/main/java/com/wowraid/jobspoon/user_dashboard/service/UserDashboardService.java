package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.dto.UserDashboardResponse;

public interface UserDashboardService {
    UserDashboardResponse getDashboardByAccountId(Long accountId);
}
