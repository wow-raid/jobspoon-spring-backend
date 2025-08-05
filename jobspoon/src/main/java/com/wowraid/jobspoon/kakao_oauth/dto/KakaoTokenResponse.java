package com.wowraid.jobspoon.kakao_oauth.dto;

/*
(참고)
{
  "access_token": "string",
  "token_type": "bearer",
  "refresh_token": "string",
  "expires_in": 21599,
  "refresh_token_expires_in": 5184000,
  "scope": "account_email profile"
}
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;

    //선택적 필드
    private String scope;
}
