package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;

public interface ProfileAppearanceService {
    AppearanceResponse getMyAppearance(Long accountId);
    AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl);
    ProfileAppearance create(AccountProfile accountProfile);
}
