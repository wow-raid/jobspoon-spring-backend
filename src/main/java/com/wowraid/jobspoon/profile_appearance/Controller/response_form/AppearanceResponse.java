package com.wowraid.jobspoon.profile_appearance.Controller.response_form;

import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppearanceResponse {
    private String photoUrl;
    private String nickname;
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

    public static AppearanceResponse of(ProfileAppearance pa){
        return AppearanceResponse.builder()
                .photoUrl(pa.getPhotoUrl())
                .nickname(pa.getCustomNickname() != null
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
