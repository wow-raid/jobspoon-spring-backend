package com.wowraid.jobspoon.userDashboard.service;

public interface InterviewSummaryService {
    long getTotalFinishedCount(Long accountId);
    long getMonthlyFinishedCount(Long accountId);
}