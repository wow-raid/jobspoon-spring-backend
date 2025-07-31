package com.wowraid.jobspoon.account_profile.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account_profile.entity.AccountProfile;
import com.wowraid.jobspoon.account_profile.entity.AdminProfile;
import com.wowraid.jobspoon.account_profile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.account_profile.service.request_form.AccountProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountProfileServiceImpl implements AccountProfileService {

    private final AccountProfileRepository accountProfileRepository;
    private final AccountRepository accountRepository;

    @Override
    public boolean createAccountProfile(Long accountId, AccountProfileRequest request) {
        log.info("createAccountProfile() 진입");

        Account account = findAccountOrThrow(accountId);
        accountProfileRepository.save(account, request.getNickname(), request.getGender(),
                request.getBirthyear(), request.getAgeRange());

        log.info("AccountProfile 생성 성공");
        return true;
    }

    @Override
    public boolean updateAccountProfileIfExists(Long accountId, AccountProfileRequest request) {
        Account account = findAccountOrThrow(accountId);
        Optional<AccountProfile> optionalProfile = accountProfileRepository.findByAccount(account);

        if (optionalProfile.isPresent()) {
            AccountProfile profile = optionalProfile.get();
            profile.updateProfile(request.getNickname(), request.getGender(),
                    request.getBirthyear(), request.getAgeRange());

            log.info("AccountProfile 수정 완료: accountId={}", accountId);
            return true;
        } else {
            log.info("AccountProfile 없음 → 신규 생성 시도");
            return createAccountProfile(accountId, request);
        }
    }

    @Override
    public boolean createAdminProfile(Long accountId, String email) {
        log.info("createAdminProfile() 진입");

        Account account = findAccountOrThrow(accountId);
        AdminProfile saved = accountProfileRepository.saveAdmin(account, email);
        log.info("AdminProfile 생성 성공: {}", saved);
        return true;
    }

    @Override
    public String findEmail(Long accountId) {
        return accountProfileRepository.findEmail(accountId).orElse(null);
    }

    @Override
    public String findRoleType(Long accountId) {
        return accountProfileRepository.findRoleType(accountId).orElse(null);
    }

    @Override
    public String findNickname(Long accountId) {
        return accountProfileRepository.findNickname(accountId).orElse(null);
    }

    @Override
    public String findGender(Long accountId) {
        return accountProfileRepository.findGender(accountId).orElse(null);
    }

    @Override
    public String findBirthyear(Long accountId) {
        return accountProfileRepository.findBirthyear(accountId).orElse(null);
    }

    private Account findAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Account: " + accountId));
    }
}

