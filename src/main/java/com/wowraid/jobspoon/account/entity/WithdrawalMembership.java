package com.wowraid.jobspoon.account.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_membership")
public class WithdrawalMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Redis에서 가져온 Account ID (실제 Account 엔티티와 FK 연결 안함)
     */
    @Column(name = "account_id", nullable = false, length = 50)
    String accountId;

    /**
     * 탈퇴 시각
     */
    @Column(name = "withdraw_at")
    private LocalDateTime withdrawAt;

    /**
     * 탈퇴 만료 시각 (보존 기간 3년)
     */
    @Column(name = "withdraw_end")
    private LocalDateTime withdrawEnd;

    protected WithdrawalMembership() {}

    public WithdrawalMembership(String accountId, LocalDateTime withdrawAt, LocalDateTime withdrawEnd) {
        this.accountId = accountId;
        this.withdrawAt = withdrawAt;
        this.withdrawEnd = withdrawEnd;
    }

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDateTime getWithdrawAt() {
        return withdrawAt;
    }

    public LocalDateTime getWithdrawEnd() {
        return withdrawEnd;
    }

    @Override
    public String toString() {
        return "WithdrawalMembership(id=" + id + ", accountId=" + accountId +
                ", withdrawAt=" + withdrawAt + ", withdrawEnd=" + withdrawEnd + ")";
    }

    public void setWithdrawAt(LocalDateTime withdrawAt) {
        this.withdrawAt = withdrawAt;
    }

    public void setWithdrawEnd(LocalDateTime withdrawEnd) {
        this.withdrawEnd = withdrawEnd;
    }
}
