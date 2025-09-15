package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.exception.NotLoggedInException;
import com.wowraid.jobspoon.account.exception.UserNotFoundException;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import com.wowraid.jobspoon.profile_appearance.Service.ProfileAppearanceService;
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
    private final AuthenticationService authenticationService;
    private final ProfileAppearanceService profileAppearanceService;


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
    public void withdraw(String userToken) {
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);

        // 로그인 상태가 아닐 때
        if (accountId == null) {
            throw new NotLoggedInException("회원이 로그인 상태가 아닙니다.");
        }

        authenticationService.deleteToken(userToken);

        profileAppearanceService.delete(accountId);

        // 계정을 찾고 삭제
        accountRepository.findById(accountId)
                .ifPresentOrElse(
                        accountRepository::delete,
                        () -> {
                            // 계정이 존재하지 않을 때
                            throw new UserNotFoundException("해당하는 계정을 찾을 수 없습니다.");
                        }
                );

    }






}
