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
    private String nickname; // AccountProfile의 닉네임
    private String email;
    private Rank rank;
    private Title title;

    @Getter
    @Builder
    public static class Rank{
        private Long id;
        private String code;
        private String displayName;
        private LocalDateTime acquiredAt;
    }

    @Getter
    @Builder
    public static class Title{
        private Long id;
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


    public static AppearanceResponse of(ProfileAppearance pa, AccountProfile ap) {
        return AppearanceResponse.builder()
                .photoUrl(pa.getPhotoUrl())
                .nickname(ap.getNickname()) // 무조건 AccountProfile.nickname 사용
                .email(ap.getEmail())
                .rank(pa.getEquippedRank() == null ? null :
                        Rank.builder()
                                .id(pa.getEquippedRank().getId())
                                .code(pa.getEquippedRank().getRankCode().name())
                                .displayName(pa.getEquippedRank().getRankCode().getDisplayName())
                                .acquiredAt(pa.getEquippedRank().getAcquiredAt())
                                .build())
                .title(pa.getEquippedTitle() == null ? null :
                        Title.builder()
                                .id(pa.getEquippedTitle().getId())
                                .code(pa.getEquippedTitle().getTitleCode().name())
                                .displayName(pa.getEquippedTitle().getTitleCode().getDisplayName())
                                .acquiredAt(pa.getEquippedTitle().getAcquiredAt())
                                .build())
                .build();
    }
}