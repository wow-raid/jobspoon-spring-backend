package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.UserLevelResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.UserLevel;
import com.wowraid.jobspoon.profile_appearance.Repository.UserLevelHistoryRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserLevelServiceImpl implements UserLevelService {

    private final UserLevelRepository userLevelRepository;
    private final UserLevelHistoryService userLevelHistoryService;

    // 유저 레벨 조회
    @Override
    @Transactional
    public UserLevelResponse getUserLevel(Long accountId) {
        UserLevel userLevel = userLevelRepository.findByAccountId(accountId)
                .orElseGet(() -> userLevelRepository.save(UserLevel.init(accountId)));
        return UserLevelResponse.fromEntity(userLevel);
    }

    // 경험치 추가 및 레벨업 처리
    @Override
    public UserLevelResponse addExp(Long accountId, int amount) {
        UserLevel userLevel = userLevelRepository.findByAccountId(accountId)
                .orElseGet(() -> userLevelRepository.save(UserLevel.init(accountId)));

        int beforeLevel = userLevel.getLevel();

        userLevel.addExp(amount);
        userLevelRepository.save(userLevel);

        // 레벨업 발생 시 기록 저장
        if (userLevel.getLevel() > beforeLevel) {
            for (int lv = beforeLevel + 1; lv <= userLevel.getLevel(); lv++) {
                userLevelHistoryService.recordLevelUp(accountId, lv);
            }
        }

        return UserLevelResponse.fromEntity(userLevel);
    }

    // 레벨 초기화 (회원 탈퇴 시 등)
    @Override
    public void resetLevel(Long accountId) {
        userLevelRepository.deleteByAccountId(accountId);
    }
}
