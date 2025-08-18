package com.wowraid.jobspoon.kakao_authentication.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public abstract class KakaoLoginResponse {
    public abstract String getHtmlResponse();
    protected static String escape(String str) {
        return str.replace("'", "\\'");
    }

}

