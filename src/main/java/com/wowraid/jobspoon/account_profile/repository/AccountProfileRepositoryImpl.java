package com.wowraid.jobspoon.account_profile.repository;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account_profile.entity.AccountProfile;
import com.wowraid.jobspoon.account_profile.entity.AdminProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountProfileRepositoryImpl implements AccountProfileRepository {

    private final AccountProfileJpaRepository accountProfileJpaRepository;
    private final AdminProfileJpaRepository adminProfileJpaRepository;

    /**
     * AccountProfile 저장
     */
    @Transactional
    public AccountProfile save(Account account, String nickname, String gender, String birthyear, String ageRange) {
        log.info("AccountProfile 저장 시도: gender={}, birthyear={}, ageRange={}", gender, birthyear, ageRange);

        gender = defaultIfBlank(gender, "None");
        birthyear = defaultIfBlank(birthyear, "0000");
        ageRange = defaultIfBlank(ageRange, "None");

        String originalNickname = defaultIfBlank(nickname, "temporary");
        String newNickname = generateUniqueNickname(originalNickname);

        AccountProfile profile = AccountProfile.builder()
                .account(account)
                .nickname(newNickname)
                .gender(gender)
                .birthyear(birthyear)
                .ageRange(ageRange)
                .build();

        return accountProfileJpaRepository.save(profile);
    }

    /**
     * AdminProfile 저장
     */
    @Transactional
    public AdminProfile saveAdmin(Account account, String email) {
        AdminProfile adminProfile = AdminProfile.builder()
                .account(account)
                .email(email)
                .build();

        return adminProfileJpaRepository.save(adminProfile);
    }

    public Optional<AccountProfile> findByAccount(Account account) {
        return accountProfileJpaRepository.findByAccount(account);
    }

    public Optional<String> findEmail(Long accountId) {
        return findProfile(accountId)
                .map(profile -> profile.getAccount().getEmail());
    }

    public Optional<String> findRoleType(Long accountId) {
        return findProfile(accountId)
                .map(profile -> profile.getAccount().getRoleType().getRoleEnum().name());
    }

    public Optional<String> findNickname(Long accountId) {
        return findProfile(accountId)
                .map(AccountProfile::getNickname);
    }

    public Optional<String> findGender(Long accountId) {
        return findProfile(accountId)
                .map(AccountProfile::getGender);
    }

    public Optional<String> findBirthyear(Long accountId) {
        return findProfile(accountId)
                .map(AccountProfile::getBirthyear);
    }

    // 🔹 내부 유틸 메서드들
    private Optional<AccountProfile> findProfile(Long accountId) {
        return accountProfileJpaRepository.findByAccount_Id(accountId);
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String generateUniqueNickname(String baseNickname) {
        String newNickname = baseNickname;
        int count = 1;

        while (accountProfileJpaRepository.existsByNickname(newNickname)) {
            newNickname = baseNickname + "_" + count++;
            log.warn("Nickname 중복, 새 시도: {}", newNickname);
        }

        return newNickname;
    }

    @Override
    public Optional<AccountProfile> findByAccountId(Long accountId) {
        return findProfile(accountId);
    }
}