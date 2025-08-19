package com.wowraid.jobspoon.kakao_authentication.service;

import java.util.Map;

public interface KakaoAuthenticationService {
    String requestKakaoOauthLink();
    Map<String, Object> getAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
}

