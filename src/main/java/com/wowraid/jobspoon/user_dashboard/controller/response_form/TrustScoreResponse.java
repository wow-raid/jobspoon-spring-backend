package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrustScoreResponse {
    private double trustScore;

    private double attendanceScore;
    private double interviewScore;
    private double quizScore;
    private double reviewScore;
    private double studyroomScore;
    private double commentScore;
    private boolean bonusApplied;
}
