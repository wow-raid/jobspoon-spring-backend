package com.wowraid.jobspoon.account_profile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountProfileResponse {
    private String email;
    private String nickname;
    private String gender;
    private String birthyear; // ← String으로 통일
}