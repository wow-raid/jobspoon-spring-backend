package com.wowraid.jobspoon.profileAppearance.Service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.profileAppearance.Controller.response.AccountSummaryResponse;
import com.wowraid.jobspoon.userAttendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {
    private final AccountRepository accountRepository;
    private final AttendanceService attendanceService;

    @Override
    @Transactional(readOnly = true)
    public AccountSummaryResponse getBasicSummary(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        String loginType = account.getAccountLoginType().getLoginType().name();
        int consecutiveAttendanceDays = attendanceService.getConsecutiveDays(accountId);

        return new AccountSummaryResponse(loginType, consecutiveAttendanceDays);
    }
}
