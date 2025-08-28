package com.wowraid.jobspoon.administer.service;

import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorServiceImpl implements AdministratorService {

    private final AccountProfileService accountProfileService;
    private final AccountRoleTypeRepository accountRoleTypeRepository;
    private final AccountLoginTypeRepository accountLoginTypeRepository;
    private final AccountService accountService;

    @Value("${admin.secret-id-key}")
    private String secretIdKey;
    @Value("${admin.secret-password-key}")
    private String secretPasswordKey;


    @Override
    public boolean validateKey(String id, String password) {
        return secretIdKey.equals(id) && secretPasswordKey.equals(password);
    }

    @Transactional
    @Override
    public void createAdminIfNotExists(String adminEmail, String adminNickname, LoginType adminLoginType) {
        Optional<AccountProfile> isAdminExist=accountProfileService.loadProfileByEmailAndLoginType(adminEmail,adminLoginType);
        if(isAdminExist.isPresent()) {
            log.info("[AdministratorService] Admin already exists");
            return;
        }
        log.info("[AdministratorService] Creating admin account. email={}, nickname={}, loginType={}",
                adminEmail, adminNickname, adminLoginType);

        AccountRoleType accountRoleType = accountRoleTypeRepository.findByRoleType(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalStateException("RoleType.ADMIN not initialized"));

        //계정 생성
        //createAccountWithRoleType(AccountRoleType accountRoleType, LoginType loginType)
        Account account = accountService.createAccountWithRoleType(accountRoleType, adminLoginType)
                .orElseThrow(() -> new IllegalStateException("Account 생성 실패"));
        //프로필 생성
        //createAccountProfile(Account account, RegisterAccountProfileRequest request)
        RegisterAccountProfileRequest profileRequest= new RegisterAccountProfileRequest(adminNickname,adminEmail);
        accountProfileService.createAccountProfile(account, profileRequest)
                .orElseThrow(() -> new IllegalStateException("AccountProfile 생성 실패"));

        log.info("[AdministratorService] Admin created successfully. email={}", adminEmail);



    }

}
