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

<<초기 DB setting>>
CREATE TABLE user_dashboard_meta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  account_id BIGINT NOT NULL,
  trust_score INT NOT NULL,
  tier VARCHAR(16) NOT NULL,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  CONSTRAINT pk_user_dashboard_meta PRIMARY KEY (id),
  CONSTRAINT uk_user_dashboard_meta_account UNIQUE (account_id)
);

 */


import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @Column(name = "trust_score", nullable = false)
    private Integer trustScore;

    @Column(name = "tier", length = 16, nullable = false)
    private String tier;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public UserDashboardMeta(Account account, String tier, Integer trustScore) {
        this.account = account;
        this.tier = tier;
        this.trustScore = trustScore;
    }

    public static UserDashboardMeta init(Account account) {
        return new UserDashboardMeta(account, "BRONZE", 70);
    }
}
