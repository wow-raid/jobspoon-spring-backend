package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.UserLevelHistoryResponse;

import java.util.List;

public interface UserLevelHistoryService {

    // 레벨업 기록 저장
    void recordLevelUp(Long accountId, int level);

    // 특정 유저의 레벨업 이력 조회
    List<UserLevelHistoryResponse> getHistory(Long accountId);
}
