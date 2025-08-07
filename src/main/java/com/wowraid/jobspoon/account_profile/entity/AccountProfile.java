package com.wowraid.jobspoon.account_profile.entity;

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_profile")
public class AccountProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, unique = true, nullable = false)
    private String nickname;

    @Column(length = 10)
    private String gender;

    @Column(length = 4)
    private String birthyear;

    @Column(name = "age_range", length = 10)
    private String ageRange;

    //    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthyear(String birthyear) {
        this.birthyear = birthyear;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public void updateProfile(String nickname, String gender, String birthyear, String ageRange) {
        if (nickname != null) this.nickname = nickname;
        if (gender != null) this.gender = gender;
        if (birthyear != null) this.birthyear = birthyear;
        if (ageRange != null) this.ageRange = ageRange;
    }
}