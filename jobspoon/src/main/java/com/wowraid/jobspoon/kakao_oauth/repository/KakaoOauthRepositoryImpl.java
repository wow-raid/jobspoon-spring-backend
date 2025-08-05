package com.wowraid.jobspoon.kakao_oauth.repository;

import com.wowraid.jobspoon.kakao_oauth.dto.KakaoTokenResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class KakaoOauthRepositoryImpl implements KakaoOauthRepository {

    @Value("${kakao.login-url}")
    private String loginUrl;

    @Value("${kakao.withdraw-url}")
    private String withdrawUrl;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-request-uri}")
    private String tokenRequestUri;

    @Value("${kakao.user-info-request-uri}")
    private String userInfoRequestUri;

    private final RestTemplate restTemplate;

    @Override
    public String getOauthLink() {
        log.info("Generating Kakao Oauth link");
        return String.format("%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                loginUrl, clientId, redirectUri);
    }

    @Override
    public String getWithdrawLink(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                withdrawUrl, HttpMethod.POST, request, String.class
        );

        return response.getBody();
    }

    @Override
    public KakaoTokenResponse getAccessToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // 필요 시 client_secret 추가?
        // 옵션: 설정에 존재할 경우만 추가

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<KakaoTokenResponse> response =
                restTemplate.postForEntity(tokenRequestUri, request, KakaoTokenResponse.class);

        return response.getBody();
    }

    @Override
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                userInfoRequestUri, HttpMethod.POST, request, KakaoUserInfoResponse.class
        );

        return response.getBody();
    }
}
