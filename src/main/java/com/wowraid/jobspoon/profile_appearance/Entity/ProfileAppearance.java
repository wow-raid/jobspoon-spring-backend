package com.wowraid.jobspoon.profile_appearance.Entity;

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

    private Long accountId;

    @Column(name = "photo_key")
    private String photoKey; // 프로필 사진 override 가능

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_title_id")
    private Title equippedTitle;


    public void setPhotoKey(String newKey) {
        this.photoKey = newKey;
    }

    public void setEquippedTitle(Title title) {
        this.equippedTitle = title;
    }

    // === 정적 팩토리 ===
    public static ProfileAppearance init(Long accountId) {
        return new ProfileAppearance(
                null,          // id (자동 생성)
                accountId,
                null,          // photoKey
                null           // equippedTitle
        );
    }
}
