package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileAppearanceRepository extends JpaRepository<ProfileAppearance, Long> {
    // AccountProfile의 PK(id) 기준으로 ProfileAppearance 찾기
    Optional<ProfileAppearance> findByAccountProfile_Id(Long accountProfileId);

    // (선택) Account의 PK(id) 기준으로도 찾고 싶다면 이 메서드 유지
    Optional<ProfileAppearance> findByAccountProfile_Account_Id(Long accountId);

    // 닉네임 중복 체크
    boolean existsByCustomNickname(String customNickname);

    void deleteByAccountProfile(AccountProfile accountProfile);
}