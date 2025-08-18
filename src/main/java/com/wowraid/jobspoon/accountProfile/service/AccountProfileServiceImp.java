package com.wowraid.jobspoon.accountProfile.service;

import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountProfileServiceImp implements AccountProfileService {

    private final AccountProfileRepository accountProfileRepository;


    @Override
    @Transactional
    public Optional<AccountProfile> loadProfileByEmailAndLoginType(String email, LoginType loginType) {
        return accountProfileRepository.findWithAccountByEmailAndLoginType(email, loginType);
    }


}
