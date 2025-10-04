package com.wowraid.jobspoon.accountProfile.service;


import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.controller.request.RegisterAccountProfileRequest;
import com.wowraid.jobspoon.accountProfile.controller.response.NicknameResponse;
import com.wowraid.jobspoon.administer.service.dto.AccountProfileRow;

import java.util.List;
import java.util.Optional;

public interface AccountProfileService {
    Optional<AccountProfile> createAccountProfile(Account account, RegisterAccountProfileRequest request);
    Optional<AccountProfile> loadProfileByEmailAndLoginType(String email, LoginType loginType);
    //2025.09.13 발키리 추가
    Optional<AccountProfile> loadProfileByEmail(String email);
    List<AccountProfileRow> getProfilesAfterId(long lastId, int limit);
    // 닉네임 수정
    Optional<NicknameResponse> updateNickname(Long accountId, String newNickname);
    Optional<AccountProfile> findById(Long id);
}
