package com.wowraid.jobspoon.profileAppearance.Controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountSummaryResponse {
    private String loginType;
    private int consecutiveAttendanceDays;
}
