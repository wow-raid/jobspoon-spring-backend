package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityAgg;

public interface UserDashboardRepository {
    ActivityAgg summarize(Long accountId);
}
