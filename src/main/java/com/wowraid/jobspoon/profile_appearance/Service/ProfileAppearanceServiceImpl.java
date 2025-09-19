package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.Title;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final TitleRepository titleRepository;
    private final AccountProfileRepository accountProfileRepository;

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

        // 타이틀 이력 삭제
        titleRepository.deleteAllByAccount_Id(accountId);

        // 프로필 외형 삭제
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
