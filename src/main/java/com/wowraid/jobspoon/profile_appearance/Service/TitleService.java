package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;

import java.util.List;

public interface TitleService {
    AppearanceResponse.Title equipTitle(Long accountId, Long titleId);
    void unequipTitle(Long accountId);
    List<AppearanceResponse.Title> getMyTitles(Long accountId);
}
