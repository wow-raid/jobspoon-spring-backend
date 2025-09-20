package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.UserLevelResponse;

public interface UserLevelService {
    UserLevelResponse getUserLevel(Long accountId);
    UserLevelResponse addExp(Long accountId, int amount);
    void resetLevel(Long accountId);
}
