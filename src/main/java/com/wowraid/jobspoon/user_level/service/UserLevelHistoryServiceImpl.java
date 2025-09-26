package com.wowraid.jobspoon.user_level.service;

import com.wowraid.jobspoon.user_level.controller.response.UserLevelHistoryResponse;
import com.wowraid.jobspoon.user_level.entity.UserLevelHistory;
import com.wowraid.jobspoon.user_level.repository.UserLevelHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLevelHistoryServiceImpl implements UserLevelHistoryService {

    private final UserLevelHistoryRepository userLevelHistoryRepository;

    @Override
    public void recordLevelUp(Long accountId, int level) {
        userLevelHistoryRepository.save(UserLevelHistory.of(accountId, level));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserLevelHistoryResponse> getHistory(Long accountId) {
        return userLevelHistoryRepository.findByAccountIdOrderByAchievedAtDesc(accountId)
                .stream()
                .map(UserLevelHistoryResponse::fromEntity)
                .toList();
    }
}
