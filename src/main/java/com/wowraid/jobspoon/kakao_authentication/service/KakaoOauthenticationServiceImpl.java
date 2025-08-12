package com.wowraid.jobspoon.kakao_authentication.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KakaoOauthenticationServiceImpl implements KakaoOauthenticationService {

    private final String loginUrl;
    private final String clientId;
    private final String redirectUri;


    public KakaoOauthenticationServiceImpl(
            @Value("${kakao.login-url}") String loginUrl,
            @Value("${kakao.client-id}") String clientId,
            @Value("${kakao.redirect-uri}") String redirectUri
    ){
        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }


    @Override
    public String requestKakaoOauthLink() {
        log.info("Kakao 소셜 로그인 시도 -> 로그인 url 호출");

        if (loginUrl == null || clientId == null || redirectUri == null) {
            throw new IllegalStateException("필수 설정 값이 누락되었습니다: loginUrl, clientId, redirectUri는 모두 필수입니다.");
        }

        return String.format("%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                loginUrl, clientId, redirectUri);
    }
}
