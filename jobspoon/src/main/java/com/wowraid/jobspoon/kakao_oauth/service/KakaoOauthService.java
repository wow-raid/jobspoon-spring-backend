package com.wowraid.jobspoon.kakao_oauth.service;

import com.wowraid.jobspoon.kakao_oauth.dto.KakaoTokenResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoUserInfoResponse;

public interface KakaoOauthService {
    String requestKakaoOauthLink();
    KakaoTokenResponse requestAccessToken(String code);
    KakaoUserInfoResponse requestUserInfo(String accessToken);
    String requestKakaoWithdrawLink(String accessToken);
    String loginOrSignUpViaKakao(String code);
}
