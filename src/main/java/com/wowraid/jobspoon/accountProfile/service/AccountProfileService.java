package com.wowraid.jobspoon.accountProfile.service;


import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;

import java.util.Optional;

public interface AccountProfileService {
    Optional<AccountProfile> loadProfileByEmailAndLoginType(String email, LoginType loginType);
}
