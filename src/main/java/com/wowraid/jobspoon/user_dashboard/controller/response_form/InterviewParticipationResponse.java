package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewParticipationResponse {
    private long interviewTotalCount;   // 누적 참여 횟수
    private long interviewMonthlyCount; // 최근 1개월 참여 횟수
}
