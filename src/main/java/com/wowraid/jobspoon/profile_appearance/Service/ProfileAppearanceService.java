package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;

public interface ProfileAppearanceService {
    AppearanceResponse getMyAppearance(Long accountId);
}
