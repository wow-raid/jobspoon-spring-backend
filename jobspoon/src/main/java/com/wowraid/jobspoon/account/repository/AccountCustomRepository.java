package com.wowraid.jobspoon.account.repository;

import com.wowraid.jobspoon.account.entity.WithdrawalMembership;

import java.time.LocalDateTime;

public interface AccountCustomRepository {
    void save(String email, String loginType);
    void saveAdmin(String email, String loginType);
    boolean deleteAccount(String accountId);
    WithdrawalMembership saveWithdrawInfo(String accountId);
    void saveWithdrawAt(String accountId, LocalDateTime time);
    void saveWithdrawEnd(String accountId, LocalDateTime time);
}
