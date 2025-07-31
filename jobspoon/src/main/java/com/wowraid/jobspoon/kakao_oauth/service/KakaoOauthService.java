package com.wowraid.jobspoon.kakao_oauth.service;

import java.util.Map;

public interface KakaoOauthService {
    String requestKakaoOauthLink();
    Map<String, Object> requestAccessToken(String code);
    Map<String, Object> requestUserInfo(String accessToken);
    String requestKakaoWithdrawLink(String accessToken);
    String loginOrSignUpViaKakao(String code);
}
