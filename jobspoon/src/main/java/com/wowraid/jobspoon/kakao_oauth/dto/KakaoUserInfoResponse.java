package com.wowraid.jobspoon.kakao_oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoResponse {
    private Long id;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
    private KakaoProperties properties;

    @Getter
    @Setter
    public static class KakaoAccount {
        private String email;
        private String gender;
        @JsonProperty("age_range")
        private String ageRange;
        private String birthyear;
    }

    @Getter
    @Setter
    public static class KakaoProperties {
        private String nickname;
    }
}
