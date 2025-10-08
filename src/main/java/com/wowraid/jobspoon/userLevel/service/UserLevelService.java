package com.wowraid.jobspoon.userLevel.service;

import com.wowraid.jobspoon.userLevel.controller.response.UserLevelResponse;

public interface UserLevelService {
    UserLevelResponse getUserLevel(Long accountId);
    UserLevelResponse addExp(Long accountId, int amount);
}
