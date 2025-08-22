package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceRateResponse {
    private double rate;
    private int attended;
    private int totoalDays;
}
