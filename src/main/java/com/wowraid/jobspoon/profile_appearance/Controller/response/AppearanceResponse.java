package com.wowraid.jobspoon.profile_appearance.Controller.response;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.profile_appearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profile_appearance.Entity.TrustScore;
import com.wowraid.jobspoon.profile_appearance.Entity.UserLevel;
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
    private Title title;
    private TrustScore trustScore;
    private UserLevel userLevel;

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
    public static class TrustScore {
        private double score;          // 총 점수
        private double attendanceRate; // 출석률
        private int monthlyInterviews;
        private int monthlyProblems;
        private int monthlyPosts;
        private int monthlyStudyrooms;
        private int monthlyComments;
    }

    @Getter
    @Builder
    public static class UserLevel {
        private int level;
        private int exp;
        private int totalExp;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhotoResponse{
        private String photoUrl;
    }


    public static AppearanceResponse of(ProfileAppearance pa, AccountProfile ap,
                                        com.wowraid.jobspoon.profile_appearance.Entity.TrustScore ts,
                                        com.wowraid.jobspoon.profile_appearance.Entity.UserLevel ul,
                                        String presignedUrl) {

        String photoUrl = (pa.getPhotoKey() != null && !pa.getPhotoKey().isEmpty())
                ? presignedUrl // presigned URL 전달
                : "/images/default_profile.png";

        return AppearanceResponse.builder()
                .photoUrl(photoUrl)
                .nickname(ap.getNickname())
                .email(ap.getEmail())
                .title(pa.getEquippedTitle() == null ? null :
                        Title.builder()
                                .id(pa.getEquippedTitle().getId())
                                .code(pa.getEquippedTitle().getTitleCode().name())
                                .displayName(pa.getEquippedTitle().getTitleCode().getDisplayName())
                                .acquiredAt(pa.getEquippedTitle().getAcquiredAt())
                                .build())
                .trustScore(ts == null ? null :
                        TrustScore.builder()
                                .score(ts.getScore())
                                .attendanceRate(ts.getAttendanceRate())
                                .monthlyInterviews(ts.getMonthlyInterviews())
                                .monthlyProblems(ts.getMonthlyProblems())
                                .monthlyPosts(ts.getMonthlyPosts())
                                .monthlyStudyrooms(ts.getMonthlyStudyrooms())
                                .monthlyComments(ts.getMonthlyComments())
                                .build())
                .userLevel(ul == null ? null :
                        UserLevel.builder()
                                .level(ul.getLevel())
                                .exp(ul.getExp())
                                .totalExp(ul.getTotalExp())
                                .build())
                .build();
    }
}