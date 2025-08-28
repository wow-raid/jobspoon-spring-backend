package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewCompletionResponse {
    private long interviewTotalCount;   // 누적 완료 횟수
    private long interviewMonthlyCount; // 최근 1개월 완료 횟수
}
