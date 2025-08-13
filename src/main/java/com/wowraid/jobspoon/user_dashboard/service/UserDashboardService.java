package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_dashboard.dto.ActivityResponse;

public interface UserDashboardService {
    //회원가입 직후 호출
    void initMetaIfAbsent(Account account);
    //조회
    ActivityResponse getDashboardByAccountId(Long accountId);
}
