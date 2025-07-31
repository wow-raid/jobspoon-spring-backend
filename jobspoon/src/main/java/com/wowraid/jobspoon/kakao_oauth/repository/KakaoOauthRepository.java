package com.wowraid.jobspoon.kakao_oauth.repository;

import java.util.Map;

public interface KakaoOauthRepository {
    String getOauthLink();
    String getWithdrawLink(String accessToken);
    Map<String, Object> getAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
}
