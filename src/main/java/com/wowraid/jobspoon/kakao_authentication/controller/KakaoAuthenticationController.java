package com.wowraid.jobspoon.kakao_authentication.controller;

import com.wowraid.jobspoon.kakao_authentication.service.KakaoOauthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kakao-authentication")
@RequiredArgsConstructor
public class KakaoAuthenticationController {

    private final KakaoOauthenticationService kakaoOauthenticationService;

    @GetMapping("/kakao/link")
    public String kakaoOauthLink() {
        return kakaoOauthenticationService.requestKakaoOauthLink();
    }



}
