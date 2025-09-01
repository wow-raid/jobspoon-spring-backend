package com.wowraid.jobspoon.profile_appearance.Controller.response_form;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppearanceResponse {
    private String photoUrl;
    /**
     * 최종적으로 화면에 노출될 닉네임
     * - customNickname이 있으면 customNickname 사용
     * - customNickname이 없으면 AccountProfile.nickname 사용 (fallback)
     *
     * 프론트에서는 이 값만 사용하면 됨
     * (기본/커스텀 구분은 API 레벨에서는 불필요)
     */
    private String customNickname;
    private Rank rank;
    private Title title;

    @Getter
    @Builder
    public static class Rank{
        private String code;
        private String displayName;
        private LocalDateTime acquiredAt;
    }

    @Getter
    @Builder
    public static class Title{
        private String code;
        private String displayName;
        private LocalDateTime acquiredAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhotoResponse{
        private String photoUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomNicknameResponse{
        /**
         * 수정된 커스텀 닉네임 실제 값 (fallback 없음)
         * - PUT /nickname API 응답 전용 DTO
         */
        private String customNickname;
    }

    public static AppearanceResponse of(ProfileAppearance pa, AccountProfile ap) {
        return AppearanceResponse.builder()
                .photoUrl(pa.getPhotoUrl())
                .customNickname(pa.getCustomNickname() != null ? pa.getCustomNickname() : ap.getNickname())
                .rank(pa.getEquippedRank() == null ? null :
                        Rank.builder()
                                .code(pa.getEquippedRank().getRankCode().name())
                                .displayName(pa.getEquippedRank().getRankCode().getDisplayName())
                                .acquiredAt(pa.getEquippedRank().getAcquiredAt())
                                .build())
                .title(pa.getEquippedTitle() == null ? null :
                        Title.builder()
                                .code(pa.getEquippedTitle().getTitleCode().name())
                                .displayName(pa.getEquippedTitle().getTitleCode().getDisplayName())
                                .acquiredAt(pa.getEquippedTitle().getAcquiredAt())
                                .build())
                .build();
    }
}
