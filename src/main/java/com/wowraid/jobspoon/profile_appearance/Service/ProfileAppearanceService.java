package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;

import java.util.List;

public interface ProfileAppearanceService {
    ProfileAppearance create(AccountProfile accountProfile);
    AppearanceResponse getMyAppearance(Long accountId);
    AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl);
    AppearanceResponse.CustomNicknameResponse updateNickname(Long accountId, String nickname);
    AppearanceResponse.Rank equipRank(Long accountId, Long rankId);
    List<AppearanceResponse.Rank> getMyRanks(Long accountId);
}
