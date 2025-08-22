package com.wowraid.jobspoon.profile_appearance.Entity;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "profile_appearance")
public class ProfileAppearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    private String customNickname; // 별명 override 가능
    private String photoUrl; // 프로필 사진 override 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_rank_id")
    private RankHistory equippedRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_title_id")
    private TitleHistory equippedTitle;
}
