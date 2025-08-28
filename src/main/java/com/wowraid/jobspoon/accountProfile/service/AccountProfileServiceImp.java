package com.wowraid.jobspoon.accountProfile.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;
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
    public Optional<AccountProfile> createAccountProfile(Account account, RegisterAccountProfileRequest request) {


        String email = requireText(request.getEmail(), "AccountProfile 생성중 이메일 값이 존재하지 않습니다");
        String nickname = requireText(request.getNickname(), "AccountProfile 생성중 닉네임 값이 존재하지 않습니다");


        AccountProfile accountProfile = new AccountProfile(account, nickname, email);
        accountProfileRepository.save(accountProfile);

        return Optional.of(accountProfile);
    }

    @Override
    @Transactional
    public Optional<AccountProfile> loadProfileByEmailAndLoginType(String email, LoginType loginType) {
        return accountProfileRepository.findWithAccountByEmailAndLoginType(email, loginType);
    }

    private String requireText(String text, String msg) {
        if(text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
        return text;
    }
}
