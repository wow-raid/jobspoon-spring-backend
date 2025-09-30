package com.wowraid.jobspoon.user_level.service;

import com.wowraid.jobspoon.user_level.controller.response.UserLevelResponse;

public interface UserLevelService {
    UserLevelResponse getUserLevel(Long accountId);
    UserLevelResponse addExp(Long accountId, int amount);
}
