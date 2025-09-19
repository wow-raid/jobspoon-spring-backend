package com.wowraid.jobspoon.profile_appearance.Entity;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile_appearance")
public class ProfileAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Long accountId;

    @Column(name = "photo_url")
    private String photoUrl; // 프로필 사진 override 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_rank_id")
    private RankHistory equippedRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_title_id")
    private TitleHistory equippedTitle;

    // === setter ===
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setEquippedRank(RankHistory rank) {
        this.equippedRank = rank;
    }

    public void setEquippedTitle(TitleHistory title) {
        this.equippedTitle = title;
    }

    // === 정적 팩토리 ===
    public static ProfileAppearance init(Long accountId) {
        return new ProfileAppearance(
                null,          // id (자동 생성)
                accountId,
                null,          // photoUrl
                null,          // equippedRank
                null           // equippedTitle
        );
    }
}
