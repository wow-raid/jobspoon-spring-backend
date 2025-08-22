package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewCompletionResponse {
    private long totalInterviewCompleted;   // 누적 완료 횟수
    private long monthlyInterviewCompleted; // 최근 1개월 완료 횟수
}
