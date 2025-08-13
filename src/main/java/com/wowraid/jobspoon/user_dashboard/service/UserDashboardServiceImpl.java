package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_dashboard.dto.ActivityAgg;
import com.wowraid.jobspoon.user_dashboard.dto.ActivityResponse;
import com.wowraid.jobspoon.user_dashboard.entity.UserDashboardMeta;
import com.wowraid.jobspoon.user_dashboard.repository.UserDashboardMetaRepository;
import com.wowraid.jobspoon.user_dashboard.repository.UserDashboardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDashboardServiceImpl implements UserDashboardService {

    // 집계 전용
    private final UserDashboardRepository dashboardRepository;
    // 스냅샷/메타
    private final UserDashboardMetaRepository metaRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void initMetaIfAbsent(Account account) {
        Long accountId = account.getId();
        if(!metaRepository.existsByAccountId(accountId)) {
            // 초기값은 정책에 맞게 조정
            UserDashboardMeta meta = UserDashboardMeta.init(account);
            metaRepository.save(meta);
        }
    }

    @Override
    public ActivityResponse getDashboardByAccountId(Long accountId) {

        ActivityAgg agg = dashboardRepository.summarize(accountId);

        UserDashboardMeta meta = metaRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Account accountRef = accountRepository.getReferenceById(accountId);
                    return metaRepository.save(UserDashboardMeta.init(accountRef));
                });

        return new ActivityResponse(
                agg.attendanceDays(),
                agg.questionTried(),
                agg.questionSolved(),
                agg.posts(),
                agg.comments(),
                meta.getTrustScore(),
                meta.getTier()
        );
    }
}
