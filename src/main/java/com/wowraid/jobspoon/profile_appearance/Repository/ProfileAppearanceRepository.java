package com.wowraid.jobspoon.profile_appearance.Repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileAppearanceRepository extends JpaRepository<ProfileAppearance, Long> {

    Optional<ProfileAppearance> findByAccountId(Long accountId);

    void deleteByAccountId(Long accountId);

    // 닉네임 중복 체크
    boolean existsByCustomNickname(String customNickname);

    // 존재 여부 확인
    boolean existsByAccountId(Long accountId);
}