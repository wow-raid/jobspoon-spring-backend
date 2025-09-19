package com.wowraid.jobspoon.profile_appearance.Service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final RankHistoryRepository rankHistoryRepository;
    private final TitleHistoryRepository titleHistoryRepository;
    private final AccountProfileRepository accountProfileRepository;

    private static final List<String> BANNED_WORDS = List.of(
            "admin", "운영자", "관리자"
    );

    /** 회원 가입 시 호출 **/
    @Override
    public Optional<ProfileAppearance> create(Long accountId) {
        ProfileAppearance pa = ProfileAppearance.init(accountId);
        return Optional.of(appearanceRepository.save(pa));
    }

    /** 회원 탈퇴 시 호출 **/
    @Override
    public void delete(Long accountId) {
        // [수정] 존재하지 않을 경우 예외 던지도록 수정
        if (!appearanceRepository.existsByAccountId(accountId)) {
            throw new IllegalArgumentException("ProfileAppearance not found for accountId=" + accountId);
        }

        // 2. 랭크 이력 삭제
        rankHistoryRepository.deleteAllByAccount_Id(accountId);

        // 3. 타이틀 이력 삭제
        titleHistoryRepository.deleteAllByAccount_Id(accountId);

        // 4. 프로필 외형 삭제
        appearanceRepository.deleteByAccountId(accountId);
    }

    /** 프로필 조회 **/
    @Override
    @Transactional(readOnly = true)
    public AppearanceResponse getMyAppearance(Long accountId) {

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseGet(() -> appearanceRepository.save(ProfileAppearance.init(accountId)));

        // [수정] Optional 중첩 제거
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("AccountProfile not found"));

        return AppearanceResponse.of(pa, ap);
    }

    /** 사진 업데이트 **/
    @Override
    public AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl) {

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        pa.setPhotoUrl(photoUrl);
        appearanceRepository.save(pa);

        return new AppearanceResponse.PhotoResponse(pa.getPhotoUrl());
    }

    /** 랭크 장착 **/
    @Override
    public AppearanceResponse.Rank equipRank(Long accountId, Long rankId){

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        RankHistory rankHistory = rankHistoryRepository.findByIdAndAccount_Id(rankId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Rank not owned by this account"));

        pa.setEquippedRank(rankHistory);

        return AppearanceResponse.Rank.builder()
                .id(rankHistory.getId())
                .code(rankHistory.getRankCode().name())
                .displayName(rankHistory.getRankCode().getDisplayName())
                .acquiredAt(rankHistory.getAcquiredAt())
                .build();
    }

    /** 랭크 목록 조회 (승급, 강등 포함한 전체 이력) **/
    @Override
    @Transactional(readOnly = true)
    public List<AppearanceResponse.Rank> getMyRanks(Long accountId){
        return rankHistoryRepository.findAllByAccount_Id(accountId).stream()
                .map(rh -> AppearanceResponse.Rank.builder()
                        .id(rh.getId())
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

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        TitleHistory titleHistory = titleHistoryRepository.findByIdAndAccount_Id(titleId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("Title not owned by this account"));

        pa.setEquippedTitle(titleHistory);

        return AppearanceResponse.Title.builder()
                .id(titleHistory.getId())
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
                        .id(th.getId())
                        .code(th.getTitleCode().name())
                        .displayName(th.getTitleCode().getDisplayName())
                        .acquiredAt(th.getAcquiredAt())
                        .build())
                .toList();
    }
}
