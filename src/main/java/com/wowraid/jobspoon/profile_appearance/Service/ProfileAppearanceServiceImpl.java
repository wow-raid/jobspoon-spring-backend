package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.RankHistory;
import com.wowraid.jobspoon.profile_appearance.Entity.TitleHistory;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.RankHistoryRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final RankHistoryRepository rankHistoryRepository;
    private final TitleHistoryRepository titleHistoryRepository;

    /** 회원 가입 시 호출 **/
    @Override
    public ProfileAppearance create(AccountProfile accountProfile) {
        ProfileAppearance pa = ProfileAppearance.init(accountProfile);
        return appearanceRepository.save(pa);
    }

    /** 프로필 조회 **/
    @Override
    @Transactional(readOnly = true)
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

    /** 닉네임 업데이트 **/
    @Override
    public AppearanceResponse.CustomNicknameResponse updateNickname(Long accountId, String nickname){
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));

        pa.setCustomNickname(nickname);
        appearanceRepository.save(pa);

        return new AppearanceResponse.CustomNicknameResponse(
                pa.getCustomNickname() != null
                    ? pa.getCustomNickname()
                    : pa.getAccountProfile().getNickname()
        );
    }

    /** 랭크 장착 **/
    @Override
    public AppearanceResponse.Rank equipRank(Long accountId, Long rankId){
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));

        RankHistory rankHistory = rankHistoryRepository.findByIdAndAccount_Id(rankId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Rank not owned by this account"));

        pa.setEquippedRank(rankHistory); // 장착

        return AppearanceResponse.Rank.builder()
                .code(rankHistory.getRankCode().name())
                .displayName(rankHistory.getRankCode().name())
                .acquiredAt(rankHistory.getAcquiredAt())
                .build();
    }

    /** 랭크 목록 조회 (승급, 강등 포함한 전체 이력) **/
    @Override
    @Transactional(readOnly = true)
    public List<AppearanceResponse.Rank> getMyRanks(Long accountId){
        return rankHistoryRepository.findAllByAccount_Id(accountId).stream()
                .map(rh -> AppearanceResponse.Rank.builder()
                        .code(rh.getRankCode().name())
                        .displayName(rh.getRankCode().getDisplayName())
                        .acquiredAt(rh.getAcquiredAt())
                        .build())
                .toList();
    }

    /** 칭호 장착 **/
    @Override
    @Transactional
    public AppearanceResponse.Title equipTitle(Long accountId, Long titleId){
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Account_Id(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile Appearance not found"));

        TitleHistory titleHistory = titleHistoryRepository.findByIdAndAccount_Id(titleId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Title not owned by this account"));

        pa.setEquippedTitle(titleHistory); // 장착

        return AppearanceResponse.Title.builder()
                .code(titleHistory.getTitleCode().name())
                .displayName(titleHistory.getTitleCode().getDisplayName())
                .acquiredAt(titleHistory.getAcquiredAt())
                .build();
    }

    /** 칭호 목록 조회 (전체 이력) **/
    @Override
    @Transactional(readOnly = true)
    public List<AppearanceResponse.Title> getMyTitles(Long accountId){
        return titleHistoryRepository.findAllByAccount_Id(accountId).stream()
                .map(th -> AppearanceResponse.Title.builder()
                        .code(th.getTitleCode().name())
                        .displayName(th.getTitleCode().getDisplayName())
                        .acquiredAt(th.getAcquiredAt())
                        .build())
                .toList();
    }
}
