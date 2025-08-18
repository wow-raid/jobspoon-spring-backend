package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityAgg;

import java.time.LocalDateTime;

public interface UserDashboardRepository {
    ActivityAgg summarize(Long accountId);
    ActivityAgg summarizeRange(Long accountId, LocalDateTime from, LocalDateTime to);
}
