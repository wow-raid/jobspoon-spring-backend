package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.register_response.RegisterResponse;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Service.ProfileAppearanceService;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final RedisCacheService redisCacheService;
    private final ProfileAppearanceService profileAppearanceService;


    @Override
    public RegisterResponse signup(String tempToken, RegisterRequestForm registerRequestForm) {


        String accessToken = redisCacheService.getValueByKey(tempToken, String.class);

        Account account = accountService.createAccount(registerRequestForm.toRegisterAccountRequest())
                .orElseThrow(() ->
                        new IllegalArgumentException("Account 생성 실패")
                );

        AccountProfile accountProfile = accountProfileService.createAccountProfile(account, registerRequestForm.toRegisterAccountProfileRequestForm())
                .orElseThrow(() ->
                        new IllegalArgumentException("AccountProfile 생성 실패")
                );

        profileAppearanceService.create(account.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("profileAppearance 생성 실패")
                );

        String userToken = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(account.getId(), accessToken);
        redisCacheService.setKeyAndValue(userToken, account.getId());

        return new RegisterResponse(accountProfile.getNickname(), accountProfile.getEmail(), userToken);
    }
}
