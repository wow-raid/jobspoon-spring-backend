package com.wowraid.jobspoon.kakao_oauth.repository;

import com.wowraid.jobspoon.kakao_oauth.dto.KakaoTokenResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoUserInfoResponse;

public interface KakaoOauthRepository {
    String getOauthLink();
    String getWithdrawLink(String accessToken);
    KakaoTokenResponse getAccessToken(String code); // 바뀐 부분
    KakaoUserInfoResponse getUserInfo(String accessToken); // 바뀐 부분
}
