package com.wowraid.jobspoon.userTitle.service;

import com.wowraid.jobspoon.userTitle.controller.response.UserTitleResponse;

import java.util.List;

public interface UserTitleService {
    UserTitleResponse equipTitle(Long accountId, Long titleId);
    void unequipTitle(Long accountId);
    List<UserTitleResponse> getMyTitles(Long accountId);
}