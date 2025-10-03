package com.wowraid.jobspoon.meta_authentication.controller;

import com.wowraid.jobspoon.meta_authentication.service.MetaAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/meta_authentication")
public class MetaAuthenticationController {

    private final MetaAuthenticationService metaAuthenticationService;


    @GetMapping("/link")
    public String metaOauthLink(){
        return metaAuthenticationService.requestKakaoOauthLink();
    }


    @GetMapping("/login")
    public String login(@RequestParam("code") String code) {
        log.info("Meta 로그인 시도");
        log.info("");
        log.info("토 큰  :  {}", code);
        log.info("");
        return "login";
    }



}
