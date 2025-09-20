package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;

import java.util.Optional;

public interface ProfileAppearanceService {
    Optional<ProfileAppearance> create(Long accountId);
    void delete(Long accountId);
    AppearanceResponse getMyAppearance(Long accountId);
    AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl);
}
