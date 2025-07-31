package com.wowraid.jobspoon.account_profile.service.request_form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountProfileRequest {
    private String nickname;
    private String gender;
    private String birthyear;
    private String ageRange;

    public AccountProfileRequest(String nickname, String gender, String birthyear, String ageRange) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthyear = birthyear;
        this.ageRange = ageRange;
    }
}
