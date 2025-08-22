package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.RankHistoryRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final TitleHistoryRepository titleHistoryRepository;
    private final RankHistoryRepository rankHistoryRepository;

    @Override
    public AppearanceResponse getMyAppearance(Long accountId) {
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));
        return AppearanceResponse.of(pa);
    }
}
