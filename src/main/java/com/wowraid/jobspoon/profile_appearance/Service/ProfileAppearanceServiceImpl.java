package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;

    /** 회원 가입 시 호출 **/
    @Override
    public ProfileAppearance create(AccountProfile accountProfile) {
        ProfileAppearance pa = ProfileAppearance.init(accountProfile);
        return appearanceRepository.save(pa);
    }

    /** 프로필 조회 **/
    @Override
    public AppearanceResponse getMyAppearance(Long accountId) {
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));

        return AppearanceResponse.of(pa);
    }

    /** 사진 업데이트 **/
    @Override
    public AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl) {
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));
        pa.setPhotoUrl(photoUrl);
        appearanceRepository.save(pa);

        return new AppearanceResponse.PhotoResponse(pa.getPhotoUrl());
    }
}
