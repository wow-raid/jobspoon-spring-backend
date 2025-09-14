package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;

import java.util.List;
import java.util.Optional;

public interface ProfileAppearanceService {

    Optional<ProfileAppearance> create(Long accountId);
    void delete(Long accountId);
    AppearanceResponse getMyAppearance(Long accountId);
    AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl);
    AppearanceResponse.CustomNicknameResponse updateNickname(Long accountId, String nickname);
    AppearanceResponse.Rank equipRank(Long accountId, Long rankId);
    List<AppearanceResponse.Rank> getMyRanks(Long accountId);
    AppearanceResponse.Title equipTitle(Long accountId, Long titleId);
    List<AppearanceResponse.Title> getMyTitles(Long accountId);
}
