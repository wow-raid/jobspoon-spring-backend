package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.Title;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TitleServiceImpl implements TitleService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final TitleRepository titleRepository;
    private final ProfileAppearanceRepository profileAppearanceRepository;

    /** 칭호 장착 **/
    @Override
    @Transactional
    public AppearanceResponse.Title equipTitle(Long accountId, Long titleId){

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        Title titleHistory = titleRepository.findByIdAndAccount_Id(titleId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Title not owned by this account"));

        pa.setEquippedTitle(titleHistory);

        return AppearanceResponse.Title.builder()
                .id(titleHistory.getId())
                .code(titleHistory.getTitleCode().name())
                .displayName(titleHistory.getTitleCode().getDisplayName())
                .acquiredAt(titleHistory.getAcquiredAt())
                .build();
    }

    /** 칭호 장착 해제 **/
    @Override
    @Transactional
    public void unequipTitle(Long accountId){
        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));
        pa.setEquippedTitle(null);
        profileAppearanceRepository.save(pa);
    }

    /** 칭호 목록 조회 (전체 이력) **/
    @Override
    @Transactional(readOnly = true)
    public List<AppearanceResponse.Title> getMyTitles(Long accountId){
        return titleRepository.findAllByAccount_Id(accountId).stream()
                .map(th -> AppearanceResponse.Title.builder()
                        .id(th.getId())
                        .code(th.getTitleCode().name())
                        .displayName(th.getTitleCode().getDisplayName())
                        .acquiredAt(th.getAcquiredAt())
                        .build())
                .toList();
    }
}
