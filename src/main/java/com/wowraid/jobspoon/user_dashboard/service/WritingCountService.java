package com.wowraid.jobspoon.user_dashboard.service;

public interface WritingCountService {
    long getStudyroomsCount(Long accountId);
    long getPostsCount(Long accountId);
    long getCommentsCount(Long accountId);
}
