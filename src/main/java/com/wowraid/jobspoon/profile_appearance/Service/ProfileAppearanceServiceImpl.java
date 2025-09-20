package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.Title;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TrustScoreRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.UserLevelRepository;
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
    private final TrustScoreRepository trustScoreRepository;
    private final UserLevelRepository userLevelRepository;

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

        // 1. 칭호 이력 삭제
        titleRepository.deleteAllByAccount_Id(accountId);

        // 2. 신뢰점수 삭제
        trustScoreRepository.deleteAllByAccountId(accountId);

        // 3. 레벨 삭제
        userLevelRepository.deleteByAccountId(accountId);

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

        // 최신 신뢰점수 가져오기
        var ts = trustScoreRepository.findTopByAccountIdOrderByCalculatedAtDesc(accountId)
                .orElse(null);

        // (레벨도 추가하면 여기서 UserLevelRepository 조회)

        return AppearanceResponse.of(pa, ap, ts, null); // 지금은 level은 null
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
}
