package com.wowraid.jobspoon.kakao_oauth.controller;

/*
https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
카카오 로그인 문서 참고
 */

import com.wowraid.jobspoon.kakao_oauth.dto.KakaoLoginLinkResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoLoginResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoOauthAccessTokenRequest;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoUnlinkResponse;
import com.wowraid.jobspoon.kakao_oauth.service.KakaoOauthService;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
public class KakaoOauthController {

    private final KakaoOauthService kakaoOauthService;
    private final RedisCacheService redisCacheService;

    //프론트에 kakao 인가 url 제공
    @GetMapping("/login-link")
    public ResponseEntity<KakaoLoginLinkResponse> getLoginLink() {
        String url = kakaoOauthService.requestKakaoOauthLink();
        return ResponseEntity.ok(new KakaoLoginLinkResponse(url));
    }

    //kakao 로그인 콜백 -> 토큰 교환 -> 회원가입/로그인 -> userToken 발급
    @PostMapping("/callback")
    public ResponseEntity<?> requestAccessToken(@RequestBody KakaoOauthAccessTokenRequest request) {
        try{
            String userToken = kakaoOauthService.loginOrSignUpViaKakao(request.getCode());
            //프론트로 userToken 응답 -> 이후 인증 수단으로 사용
            return ResponseEntity.ok(new KakaoLoginResponse(userToken));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //kakao 연동 해제 요청
    @GetMapping("/unlink")
    public ResponseEntity<KakaoUnlinkResponse> unlink(@RequestHeader("Authorization") String bearerToken) {
        String userToken = bearerToken.replace("Bearer ", "");

        //Redis에서 userToken → accountId, accountId → accessToken 역참조
        String accountId = redisCacheService.getValueByKey(userToken, String.class);
        String accessToken = redisCacheService.getValueByKey(accountId, String.class);

        //Kakao Unlink API 호출 (access token 사용)
        String result = kakaoOauthService.requestKakaoWithdrawLink(accessToken);
        //연결 해제 응답 반환
        return ResponseEntity.ok(new KakaoUnlinkResponse(result));
    }
}
