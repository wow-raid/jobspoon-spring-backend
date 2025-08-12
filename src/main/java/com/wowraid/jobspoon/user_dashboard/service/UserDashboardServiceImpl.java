package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.user_dashboard.dto.UserDashboardResponse;
import com.wowraid.jobspoon.user_dashboard.entity.UserDashboard;
import com.wowraid.jobspoon.user_dashboard.repository.UserDashboardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDashboardServiceImpl implements UserDashboardService {

    private final UserDashboardRepository dashboardRepository;

    @Transactional
    public void createIfAbsent(Account account) {
        if(!dashboardRepository.existsByAccount_Id(account.getId())) {
            UserDashboard dashboard = UserDashboard.initFor(account);
            dashboardRepository.save(dashboard);
        }
    }

    @Override
    public UserDashboardResponse getDashboardByAccountId(Long accountId) {
        UserDashboard dashboard = dashboardRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("대시보드 정보가 존재하지 않습니다."));

        return UserDashboardResponse.from(dashboard);
    }
}
