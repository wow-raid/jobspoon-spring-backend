package com.wowraid.jobspoon.profile_appearance.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
ProfileAppearance / RankHistory / TitleHistory → Account와 직접 연관관계
(상태 이력)

NicknameHistory → 그냥 accountId 숫자만 저장 (로그 성격이므로)
(행동 로그, 기록 추적용)
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nickname_history")
public class NicknameHistory {

    @Id
    @GeneratedValue
    private Long id;

    private Long accountId;

    private String customNickname;

    private LocalDateTime changedAt;

    public NicknameHistory(Long accountId, String nickname, LocalDateTime changedAt) {
        this.accountId = accountId;
        this.customNickname = nickname;
        this.changedAt = changedAt;
    }
}
