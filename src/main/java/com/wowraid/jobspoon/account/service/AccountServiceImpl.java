package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountLoginTypeRepository accountLoginTypeRepository;
    private final AccountRoleTypeRepository accountRoleTypeRepository;
    private final RedisCacheService redisCacheService;


    @Override
    @Transactional
    public Optional<Account> createAccount(RegisterAccountRequest requestForm) {

        AccountRoleType accountRoleType = accountRoleTypeRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalArgumentException("RoleType.USER 가 DB에 존재하지 않습니다"));

        LoginType loginType = requestForm.getLoginType();
        return createAccountWithRoleType(accountRoleType,loginType);


    }
    public Optional<Account> createAccountWithRoleType(AccountRoleType accountRoleType, LoginType loginType) {


        log.info("로그인 타입 : {}", loginType);
        AccountLoginType accountLoginType = accountLoginTypeRepository.findByLoginType(loginType)
                .orElseThrow(() -> new IllegalArgumentException("LoginType.%s 가 DB에 존재하지 않습니다".formatted(loginType)));

        Account account = new Account(accountRoleType, accountLoginType);
        accountRepository.save(account);
        return Optional.of(account);
    }

    @Override
    public boolean logout(String userToken) {
        try {
            log.info("로그아웃 시도");
            boolean deleteTokenResult = deleteToken(userToken);
            if (deleteTokenResult) {
                return true;
            }else {
                return false;
            }
        }catch (Exception e) {
            log.info("로그아웃 문제 발생");
            return false;
        }

    }

    @Override
    public boolean deleteToken(String userToken) {
        try {
            log.info("유저토큰 삭제 시도");
            String accountId = redisCacheService.getValueByKey(userToken, String.class);
            redisCacheService.deleteByKey(accountId);
            redisCacheService.deleteByKey(userToken);
            return true;
        }catch (Exception e) {
            log.info("유저토큰 삭제시 문제 발생");
            return false;

        }

    }


}
