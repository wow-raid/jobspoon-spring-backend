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

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserDashboardServiceImpl implements UserDashboardService {

    // 집계 전용
    private final UserDashboardRepository dashboardRepository;
    // 스냅샷/메타
    private final UserDashboardMetaRepository metaRepository;
    // init용
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

    /** 기존 조회 API는 그대로: 집계 + 메타 합쳐서 내려주기 */
    @Override
    @Transactional
    public ActivityResponse getDashboardByAccountId(Long accountId) {

        UserDashboardMeta meta = metaRepository.findByAccountId(accountId)
                .orElseGet(() -> {
                    Account accountRef = accountRepository.getReferenceById(accountId);
                    return metaRepository.save(UserDashboardMeta.init(accountRef));
                });

        // 워터마크 기반 범위 집계: [from, now)
        var now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        var from = meta.getLastAppliedAt() != null ? meta.getLastAppliedAt() : meta.getCreatedAt();

        ActivityAgg deltaAgg = dashboardRepository.summarizeRange(accountId, from, now);

        // 정책: 집계→점수 Δ
        int delta = 0;
        delta += deltaAgg.attendanceDays() * 2;     // 출석 하루당 +2
        delta += (deltaAgg.questionSolved() * 3) / 2; // 정답 1.5(내림)
        delta += deltaAgg.questionTried();          // 시도 +1/건
        delta += deltaAgg.posts() * 2;              // 게시글 +2/건
        delta += deltaAgg.comments();               // 댓글 +1/건

        meta.applyDeltaAndAdvanceWatermark(delta, now);

        // 표시용 집계
        ActivityAgg viewAgg = dashboardRepository.summarize(accountId);

        return new ActivityResponse(
                viewAgg.attendanceDays(),
                viewAgg.questionTried(),
                viewAgg.questionSolved(),
                viewAgg.posts(),
                viewAgg.comments(),
                meta.getTrustScore(),
                meta.getTier() // Enum이면 .name()
        );
    }
}
