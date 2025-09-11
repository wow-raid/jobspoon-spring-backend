package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.AccountRoleType;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;

import java.util.Optional;

public interface AccountService {

    Optional<Account> createAccount(RegisterAccountRequest requestForm);
    Optional<Account> createAccountWithRoleType(AccountRoleType accountRoleType, LoginType loginType);


    void withdraw(String userToken);

}
