package com.wowraid.jobspoon.account_profile.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account_profile.service.request_form.AccountProfileRequest;

public interface AccountProfileService {

    boolean createAccountProfile(Long accountId, AccountProfileRequest request);
    boolean createAdminProfile(Long accountId, String email);
    String findEmail(Long accountId);
    String findRoleType(Long accountId);
    String findNickname(Long accountId);
    String findGender(Long accountId);
    String findBirthyear(Long accountId);
    boolean updateAccountProfileIfExists(Long accountId, AccountProfileRequest request);
}
