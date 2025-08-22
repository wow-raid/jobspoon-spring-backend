package com.wowraid.jobspoon.accountProfile.service;


import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;

import java.util.Optional;

public interface AccountProfileService {
    Optional<AccountProfile> createAccountProfile(Account account, RegisterAccountProfileRequest request);
    Optional<AccountProfile> loadProfileByEmailAndLoginType(String email, LoginType loginType);
}
