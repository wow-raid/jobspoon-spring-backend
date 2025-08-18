package com.wowraid.jobspoon.user_dashboard.entity;


/*
[메타 테이블]
- account_id
- trust_score 초기값
- tier 초기값
→ 회원가입 시 insert

[집계 전용 쿼리]
- 출석일, 문제, 게시글, 댓글 계산

[조회 서비스]
- 메타에서 tier/trust 가져오고
- 집계 쿼리에서 나머지 값 가져와서 합침
 */


import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_dashboard.dto.Tier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_dashboard_meta")
public class UserDashboardMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @Column(name = "trust_score", nullable = false)
    private Integer trustScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 16, nullable = false)
    private Tier tier;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_applied_at")
    private LocalDateTime lastAppliedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserDashboardMeta(Account account, Tier tier, Integer trustScore) {
        this.account = account;
        this.tier = tier;
        this.trustScore = trustScore;
    }

    public static UserDashboardMeta init(Account account) {
        UserDashboardMeta m = new UserDashboardMeta();
        m.account = account;
        m.trustScore = 50;
        m.tier = Tier.BRONZE;
        m.lastAppliedAt = null;
        return m;
    }

    public void applyDeltaAndAdvanceWatermark(int delta, LocalDateTime to) {
        if (delta != 0) {
            int next = Math.max(0, this.trustScore + delta);
            this.trustScore = next;
            this.tier = Tier.of(next); // tier가 String이면 .name(), Enum이면 = Tier.of(next)
        }
        // delta가 0이어도 워터마크는 이동해야 동일 범위가 다시 계산되지 않음
        this.lastAppliedAt = to;
    }
}
