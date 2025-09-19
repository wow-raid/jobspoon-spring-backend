package com.wowraid.jobspoon.user_dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_level")
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private int level;      // 현재 레벨
    private int exp;        // 현재 경험치
    private int totalExp;   // 누적 경험치

    private LocalDateTime updatedAt;

    /** 경험치 추가 */
    public void addExp(int amount){
        this.exp += amount;
        this.totalExp += amount;
        checkLevelUp();
        this.updatedAt = LocalDateTime.now();
    }

    /** 레벨업 체크 */
    private void checkLevelUp(){
        // 예시: 레벨업 기준 = level * 100
        while (exp >= level * 100) {
            exp -= level * 100;
            level++;
        }
    }

    /** 초기화 팩토리 */
    public static UserLevel init(Long accountId) {
        return new UserLevel(null, accountId, 1, 0, 0, LocalDateTime.now());
    }
}