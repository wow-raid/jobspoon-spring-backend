package com.wowraid.jobspoon.userDashboard.service;

public interface WritingCountService {
    long getStudyroomsCount(Long accountId);
    long getPostsCount(Long accountId);
    long getCommentsCount(Long accountId);
}
