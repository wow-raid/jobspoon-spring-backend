package com.wowraid.jobspoon.administer.service;

import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorServiceImpl implements AdministratorService {

    private final AccountProfileService accountProfileService;
    private final AccountProfileRepository accountProfileRepository;
    private final AccountRoleTypeRepository accountRoleTypeRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final RedisCacheService redisCacheService;

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

    @Override
    public boolean isAdminByUserToken(String userToken) {
        if(userToken == null || userToken.isBlank()) {
            log.info("[AdministratorService] UserToken is null or empty");
            return false;
        }
//        log.info("[AdministratorService] UserToken={}", userToken);
        //요청을 통해 들어온 userToken을 redis에 조회하여 accountid를 얻는다
        Long accountId= redisCacheService.getValueByKey(userToken, Long.class);
        log.info("[AdministratorService] AccountId={}", accountId);
        return accountRepository.findById(accountId)
                .map(a -> a.getAccountRoleType() != null
                        && a.getAccountRoleType().getRoleType() == RoleType.ADMIN)
                .orElse(false);
    }

    @Override
    public String createTemporaryAdminToken() {
        try{
            final String createdTemporaryAdminToken= "temporaryAdminToken";
            final String tempToken= UUID.randomUUID().toString();

            redisCacheService.setKeyAndValue(tempToken,createdTemporaryAdminToken, Duration.ofMinutes(5));
            return tempToken;
        } catch (Exception e) {
            throw new RuntimeException("TemporaryAdminToken 발행중 오류 발생 " +  e.getMessage());
        }
    }
}
/*
    @Override
    public String createTemporaryUserTokenWithAccessToken(String accessToken) {

        try {
            String tempToken = UUID.randomUUID().toString();
            redisCacheService.setKeyAndValue(tempToken, accessToken, Duration.ofMinutes(5));
            return tempToken;
        }catch (Exception e) {
            throw new RuntimeException("TemporaryUserToken 발행중 오류 발생 " +  e.getMessage());
        }


    }
 */