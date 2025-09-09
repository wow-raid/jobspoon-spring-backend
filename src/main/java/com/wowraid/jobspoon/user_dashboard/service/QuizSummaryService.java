package com.wowraid.jobspoon.user_dashboard.service;

public interface QuizSummaryService {
    long getTotalCount(Long accountId);
    long getMonthlyCount(Long accountId);
}
