package com.wowraid.jobspoon.userDashboard.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDashboardSummaryResponse {
    private double attendanceRate;
    private int monthlyInterviews;
    private int monthlyProblems;
    private int monthlyStudyrooms;
}
