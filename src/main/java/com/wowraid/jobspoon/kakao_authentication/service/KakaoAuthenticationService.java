package com.wowraid.jobspoon.kakao_authentication.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.kakao_authentication.service.response.KakaoLoginResponse;

import java.util.Map;

public interface KakaoAuthenticationService {
    String requestKakaoOauthLink();
    Map<String, Object> getAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
    KakaoLoginResponse handleLogin(String code);
    String extractNickname(Map<String, Object> userInfo);
    String extractEmail(Map<String, Object> userInfo);
    String createUserTokenWithAccessToken(Long accountId, String accessToken);
    String createTemporaryUserTokenWithAccessToken(String accessToken);

}

