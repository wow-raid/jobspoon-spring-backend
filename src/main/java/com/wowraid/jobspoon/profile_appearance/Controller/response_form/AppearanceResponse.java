package com.wowraid.jobspoon.profile_appearance.Controller.response_form;

import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppearanceResponse {
    private String photoUrl;
    private String customNickname;
    private Rank rank;
    private Title title;

    @Getter
    @Builder
    public static class Rank{
        private String code;
        private String displayName;
    }

    @Getter
    @Builder
    public static class Title{
        private String code;
        private String displayName;
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
        private String customNickname;
    }

    public static AppearanceResponse of(ProfileAppearance pa){
        return AppearanceResponse.builder()
                .photoUrl(pa.getPhotoUrl())
                .customNickname(pa.getCustomNickname() != null
                        ? pa.getCustomNickname()
                        : pa.getAccountProfile().getNickname())
                .rank(pa.getEquippedRank() == null ? null :
                        Rank.builder()
                                .code(pa.getEquippedRank().getRankCode().name())
                                .displayName(pa.getEquippedRank().getRankCode().getDisplayName())
                                .build())
                .title(pa.getEquippedTitle() == null ? null :
                        Title.builder()
                                .code(pa.getEquippedTitle().getTitleCode().name())
                                .displayName(pa.getEquippedTitle().getTitleCode().getDisplayName())
                                .build())
                .build();
    }
}
