package com.wowraid.jobspoon.profile_appearance.Service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Entity.NicknameHistory;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.RankHistory;
import com.wowraid.jobspoon.profile_appearance.Entity.TitleHistory;
import com.wowraid.jobspoon.profile_appearance.Repository.NicknameHistoryRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.RankHistoryRepository;
import com.wowraid.jobspoon.profile_appearance.Repository.TitleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final RankHistoryRepository rankHistoryRepository;
    private final TitleHistoryRepository titleHistoryRepository;
    private final NicknameHistoryRepository nicknameHistoryRepository;
    private final AccountProfileRepository accountProfileRepository;

    private static final List<String> BANNED_WORDS = List.of(
            "admin", "운영자", "관리자"
    );

    /** 회원 가입 시 호출 **/
    @Override
    public ProfileAppearance create(AccountProfile accountProfile) {
        ProfileAppearance pa = ProfileAppearance.init(accountProfile);
        return appearanceRepository.save(pa);
    }

    /** 회원 탈퇴 시 호출 **/
    @Override
    public void delete(AccountProfile accountProfile) {
        appearanceRepository.deleteByAccountProfile(accountProfile);
    }

    /** 프로필 조회 **/
    @Override
    @Transactional(readOnly = true)
    public AppearanceResponse getMyAppearance(Long accountId) {
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account Profile not found"));

        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Id(ap.getId())
                .orElseGet(() -> appearanceRepository.save(ProfileAppearance.init(ap)));

        return AppearanceResponse.of(pa, ap);
    }

    /** 사진 업데이트 **/
    @Override
    public AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoUrl) {
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account Profile not found"));

        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Id(ap.getId())
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        pa.setPhotoUrl(photoUrl);
        appearanceRepository.save(pa);

        return new AppearanceResponse.PhotoResponse(pa.getPhotoUrl());
    }

    /** 닉네임 업데이트 **/
    @Override
    public AppearanceResponse.CustomNicknameResponse updateNickname(Long accountId, String newNickname){
        if(newNickname == null || newNickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 비워둘 수 없습니다.");
        }

        String trimmed = newNickname.trim();

        // length 제한
        if(trimmed.length() < 2 || trimmed.length() > 8) {
            throw new IllegalArgumentException("닉네임은 2자 이상 8자 이하만 가능합니다.");
        }

        // 허용 문자만
        if(!trimmed.matches("^[가-힣a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.");
        }

        // 금칙어 검증
        for(String banned : BANNED_WORDS) {
            if(trimmed.toLowerCase().contains(banned)) {
                throw new IllegalArgumentException("사용할 수 없는 단어가 포함되어 있습니다.");
            }
        }

        // 중복 닉네임 검증
        if(appearanceRepository.existsByCustomNickname(trimmed)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 계정 정보 조회
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account Profile not found"));

        // 기본 닉네임과 동일 여부
        if (trimmed.equals(ap.getNickname())) {
            throw new IllegalArgumentException("기본 닉네임과 동일한 값은 사용할 수 없습니다.");
        }

        // 닉네임 변경 횟수 제한(한 달 3번)
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        long changes = nicknameHistoryRepository.countByAccountIdAndChangedAtAfter(accountId, oneMonthAgo);
        if (changes >= 3) {
            throw new IllegalArgumentException("닉네임은 한 달에 최대 3번까지만 변경할 수 있습니다.");
        }

        // 프로필 가져오기
        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Id(ap.getId())
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        // 저장
        pa.setCustomNickname(trimmed);

        // 닉네임 변경 기록 저장
        nicknameHistoryRepository.save(new NicknameHistory(accountId, trimmed, LocalDateTime.now()));

        return new AppearanceResponse.CustomNicknameResponse(trimmed);
    }

    /** 랭크 장착 **/
    @Override
    public AppearanceResponse.Rank equipRank(Long accountId, Long rankId){
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("AccountProfile not found"));

        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Id(ap.getId())
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
        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("AccountProfile not found"));

        ProfileAppearance pa = appearanceRepository.findByAccountProfile_Id(ap.getId())
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
