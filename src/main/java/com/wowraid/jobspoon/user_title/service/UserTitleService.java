package com.wowraid.jobspoon.user_title.service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.user_title.controller.response.UserTitleResponse;

import java.util.List;

public interface UserTitleService {
    UserTitleResponse equipTitle(Long accountId, Long titleId);
    void unequipTitle(Long accountId);
    List<UserTitleResponse> getMyTitles(Long accountId);
}