package com.wowraid.jobspoon.user_dashboard.service;

public interface WritingCountService {
    long getStudyroomCount(Long accountId);
    long getReviewCount(Long accountId);
    long getCommentCount(Long accountId);
}
