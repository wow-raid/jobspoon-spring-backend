package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityResponse;

public interface UserDashboardRepository {
    ActivityResponse summarize(Long accountId);
}
