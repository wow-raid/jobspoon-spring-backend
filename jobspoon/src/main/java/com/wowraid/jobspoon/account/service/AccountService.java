package com.wowraid.jobspoon.account.service;

import java.time.LocalDateTime;

public interface AccountService {
    void createAccount(String email, String loginType);
    void createGuestAccount(String email, String loginType);
    void createAdminAccount(String email, String loginType);
    boolean checkEmailDuplication(String email);
    String findEmail(Long accountId);
    void createWithdrawAccount(String accountId);
    void createWithdrawAt(String accountId, LocalDateTime time);
    void createWithdrawEnd(String accountId, LocalDateTime time);
    boolean withdraw(String accountId);
    long countEmail(String guestEmailPrefix);
}