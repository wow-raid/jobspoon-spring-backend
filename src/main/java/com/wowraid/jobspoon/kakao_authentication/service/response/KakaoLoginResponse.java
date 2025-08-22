package com.wowraid.jobspoon.kakao_authentication.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class KakaoLoginResponse {
    public static KakaoLoginResponse of(boolean isNewUser, String token, String nickname, String email, String origin) {
        return isNewUser
                ? new NewUserKakaoLoginResponse(isNewUser, token, nickname, email, origin)
                : new ExistingUserKakaoLoginResponse(isNewUser, token, nickname, email, origin);
    }
    public abstract String getHtmlResponse();
    protected static String escape(String str) {
        return str.replace("'", "\\'");
    }

}

