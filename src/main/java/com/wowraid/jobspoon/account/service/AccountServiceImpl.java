package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
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


    @Override
    @Transactional
    public Optional<Account> createAccount(RegisterAccountRequest requestForm) {


        AccountRoleType accountRoleType = accountRoleTypeRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalArgumentException("RoleType.USER 가 DB에 존재하지 않습니다"));


        LoginType loginType = requestForm.getLoginType();


        AccountLoginType accountLoginType = accountLoginTypeRepository.findByLoginType(loginType)
                .orElseThrow(() -> new IllegalArgumentException("LoginType.%s 가 DB에 존재하지 않습니다".formatted(loginType)));


        Account account = new Account(accountRoleType, accountLoginType);
        accountRepository.save(account);
        return Optional.of(account);


    }
}
