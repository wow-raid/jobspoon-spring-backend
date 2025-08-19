package com.wowraid.jobspoon.kakao_oauthentication.service;


import com.wowraid.jobspoon.kakao_authentication.service.KakaoAuthenticationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class KakaoAuthenticationServiceTest {

    @Mock
    RestTemplate restTemplate;


    private KakaoAuthenticationServiceImpl kakaoAuthenticationService;

    private String loginUrl = "https://kauth.kakao.com";
    private String clientId = "test-client-id";
    private String redirectUri = "http://localhost:8080/oauth/kakao/callback";
    private final String TEST_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String TEST_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";



    @BeforeEach
    void setUp() {
        kakaoAuthenticationService = new KakaoAuthenticationServiceImpl(
                loginUrl,
                clientId,
                redirectUri,
                TEST_TOKEN_URL,
                TEST_USER_INFO_URL,
                restTemplate
        );
    }

    @Test
    @DisplayName("로그인_요청_시_KakaoOauth_로그인_url_응답")
    void 로그인_요청_시_KakaoOauth_로그인_url_응답() {

        // given
        String oauthLink = kakaoAuthenticationService.requestKakaoOauthLink();

        // when
        String link = String.format("%s/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                loginUrl, clientId, redirectUri);
        // then
        Assertions.assertEquals(oauthLink, link);




    }



    @Test
    void loginUrl이_null일_경우_IllegalStateException_발생() {
        // given
        ReflectionTestUtils.setField(kakaoAuthenticationService, "loginUrl", null);
        ReflectionTestUtils.setField(kakaoAuthenticationService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(kakaoAuthenticationService, "redirectUri", "http://localhost:8080/callback");

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            kakaoAuthenticationService.requestKakaoOauthLink();
        });
    }

    @Test
    void clientId가_null일_경우_IllegalStateException_발생() {
        // given
        ReflectionTestUtils.setField(kakaoAuthenticationService, "loginUrl", "https://kauth.kakao.com");
        ReflectionTestUtils.setField(kakaoAuthenticationService, "clientId", null);
        ReflectionTestUtils.setField(kakaoAuthenticationService, "redirectUri", "http://localhost:8080/callback");

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            kakaoAuthenticationService.requestKakaoOauthLink();
        });
    }

    @Test
    void redirectUri가_null일_경우_IllegalStateException_발생() {
        // given
        ReflectionTestUtils.setField(kakaoAuthenticationService, "loginUrl", "https://kauth.kakao.com");
        ReflectionTestUtils.setField(kakaoAuthenticationService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(kakaoAuthenticationService, "redirectUri", null);

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            kakaoAuthenticationService.requestKakaoOauthLink();
        });
    }


    @Test
    void 유효한_코드일_때_AccessToken을_반환헙니다(){

        // given
        String testCode = "test_code";
        String expectedToken = "test_access_token";

        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", expectedToken);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(TEST_TOKEN_URL),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(responseEntity);

        // when
        Map<String, Object> actualResponse = kakaoAuthenticationService.getAccessToken(testCode);

        // then
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedToken,actualResponse.get("access_token"));



    }


    @Test
    void 유효한_AccessToken일_때_유저_정보를_반환합니다(){

        // given
        String testAccessToken = "test_access_token";
        String expectedEmail = "test_expected_email";
        String expectedNickname = "test_expected_nickname";

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("kakao_account", Map.of("email", expectedEmail, "nickname", expectedNickname));
        ResponseEntity<Map> ResponseEntity = new ResponseEntity<>(userInfo, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(TEST_USER_INFO_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity);

        // when
        Map<String, Object> actualUserInfo = kakaoAuthenticationService.getUserInfo(testAccessToken);

        // then
        Assertions.assertNotNull(actualUserInfo);
        Map<String, Object> kakaoAccount = (Map<String, Object>) actualUserInfo.get("kakao_account");
        Assertions.assertEquals(expectedEmail, kakaoAccount.get("email"));
        Assertions.assertEquals(expectedNickname, kakaoAccount.get("nickname"));



    }




}
