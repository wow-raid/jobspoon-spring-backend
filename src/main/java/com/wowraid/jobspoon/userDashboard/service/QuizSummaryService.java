package com.wowraid.jobspoon.userDashboard.service;

public interface QuizSummaryService {
    long getTotalCount(Long accountId);
    long getMonthlyCount(Long accountId);
}
