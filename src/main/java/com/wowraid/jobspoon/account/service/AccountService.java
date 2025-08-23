package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;

import java.util.Optional;

public interface AccountService {

    Optional<Account> createAccount(RegisterAccountRequest requestForm);
}
