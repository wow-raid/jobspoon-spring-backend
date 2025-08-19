package com.wowraid.jobspoon.kakao_authentication.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class KakaoAuthenticationServiceImpl implements KakaoAuthenticationService {

    private final String loginUrl;
    private final String clientId;
    private final String redirectUri;
    private final String tokenRequestUri;
    private final String userInfoRequestUri;

    private final RestTemplate restTemplate;


    public KakaoAuthenticationServiceImpl(
            @Value("${kakao.login-url}") String loginUrl,
            @Value("${kakao.client-id}") String clientId,
            @Value("${kakao.redirect-uri}") String redirectUri,
            @Value("${kakao.token-request-uri}") String tokenRequestUri,
            @Value("${kakao.user-info-request-uri}") String userInfoRequestUri,
            RestTemplate restTemplate
    ) {
        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.tokenRequestUri = tokenRequestUri;
        this.userInfoRequestUri = userInfoRequestUri;

        this.restTemplate = restTemplate;
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

    // authorize에 의해 KAKAO 벤더로부터 수신한 인증 code를 가지고 access token을 요청하는 과정입니다.
    @Override
    public Map<String, Object> getAccessToken(String code) {


        // 헤더 구성 ( 요청 헤더 부분 )
        // Content-Type: application/x-www-form-urlencoded 로 구성되어 있습니다
        // 이는 Spring에서 `MediaType.APPLICATION_FORM_URLENCODED` 로 표현
        // Post 방식으로 전송하겠다는 의미를 내포하고 있습니다
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // MultiValueMap -> Key, Value를 여러 다발로 받기 위해 사용합니다
        // Content-Type: application/x-www-form-urlencoded 로 구성시 MultiValueMap가 기본이기 떄문에 키 값이 중복되지 않아도 MultiValueMap로 사용
        // Kakao 밴더에서 요구하는 형식의 데이터셋 구성
        // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token <- 형식
        // 위 링크의 본문(Body)에 해당하는 내용을 맞춰 보내도록 구성하는 작업
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);
        formData.add("client_secret", "");

        // HttpEntity의 경우 HTTP 헤더 + 본문(Body)로 구성
        // 첫 번쨰 인자 - Body, 두 번째 인자 - Header
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, httpHeaders);


        // restTemplate을 사용 -> Spring이 Client로서 외부 서버에게 요청하는 상황이 됩니다
        // restTemplate.exchange()는 HTTP요청을 보내고 응답을 받는 메서드
        // tokenRequestUri는 Kakao에게 토큰을 요청할 때 사용하는 URI 주소
        // Kakao 벤더에서 반드시 POST로 요청하라고 명시하였음
        // 4번째 인자는 결과를 Map 형태의 Key, Value로 받겠다는 의미입니다
        ResponseEntity<Map> response = restTemplate.exchange(
                tokenRequestUri, HttpMethod.POST, httpEntity, Map.class
        );

        log.info("AccessToken응답 받기 성공 AccessToken : {}", response.getBody());

        return response.getBody();
    }

    @Override
    public Map<String, Object> getUserInfo(String accessToken) {

        // GET/POST	https://kapi.kakao.com/v2/user/me
        // 요청: 액세스 토큰 방식 헤더 파트를 보면
        // Key로 "Authorization" 을 어떻게 구성해야 하는지 나옵니다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoRequestUri, HttpMethod.GET, httpEntity, Map.class
        );

        log.info("User Info 요청 : {}", response.getBody());


        return response.getBody();
    }
}
