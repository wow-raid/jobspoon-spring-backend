package com.wowraid.jobspoon.user_dashboard.dto;

import com.wowraid.jobspoon.user_dashboard.entity.UserDashboard;

public record UserDashboardResponse (
    double attendanceRate,
    Long mockInterviewCount,
    Long problemSolvingCount,
    Long postCount,
    int trustScore,
    String trustGrade
){
    public static UserDashboardResponse from(UserDashboard dashboard) {
        return new UserDashboardResponse(
                dashboard.getAttendanceRate(),
                dashboard.getMockInterviewCount(),
                dashboard.getProblemSolvingCount(),
                dashboard.getPostCount(),
                dashboard.getTrustScore(),
                dashboard.getTrustGrade()
        );
    }
}
