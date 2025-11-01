package com.wowraid.jobspoon.profileAppearance.Service;

import com.wowraid.jobspoon.profileAppearance.Controller.response.AccountSummaryResponse;

public interface AccountSummaryService {
    AccountSummaryResponse getBasicSummary(Long accountId);
}
