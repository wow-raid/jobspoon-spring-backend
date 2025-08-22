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
@Table(name = "profile_appearance",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_custom_nickname", columnNames = "custom_nickname")
        })
public class ProfileAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Column(name = "custom_nickname", length = 8)
    private String customNickname; // 별명 override 가능

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

    public void setCustomNickname(String customNickname) {
        this.customNickname = customNickname;
    }

    public void setEquippedRank(RankHistory rank) {
        this.equippedRank = rank;
    }

    public void setEquippedTitle(TitleHistory title) {
        this.equippedTitle = title;
    }

    // === 정적 팩토리 ===
    public static ProfileAppearance init(AccountProfile accountProfile) {
        return new ProfileAppearance(
                null,          // id (자동 생성)
                accountProfile,
                null,          // customNickname
                null,          // photoUrl
                null,          // equippedRank
                null           // equippedTitle
        );
    }
}
