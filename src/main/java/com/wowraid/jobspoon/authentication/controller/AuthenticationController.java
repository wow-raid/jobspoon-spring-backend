package com.wowraid.jobspoon.authentication.controller;

import com.wowraid.jobspoon.authentication.controller.response_form.TokenAuthenticationExpiredResponseForm;
import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @GetMapping("/token/verification")
    public ResponseEntity<TokenAuthenticationExpiredResponseForm> verifyToken(
            @CookieValue(name = "userToken", required = false) String userToken) {

        log.info("");
        log.info("토큰 검증 시작");
        log.info("");


        if (userToken == null || userToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenAuthenticationExpiredResponseForm(false));
        }

        boolean verification = authenticationService.verification(userToken);

        if (verification) {
            log.info("검증 완료==");
            return ResponseEntity.ok(new TokenAuthenticationExpiredResponseForm(true));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenAuthenticationExpiredResponseForm(false));
        }
    }


    @PostMapping("/logout")
    public String logout(@CookieValue(name = "userToken", required = false) String userToken) {
        log.info("로그아웃 호출 = {} ", userToken);
        boolean logoutResult = authenticationService.logout(userToken);
        if (logoutResult) {
            return "success";
        }else{
            return "fail";
        }

    }


}
