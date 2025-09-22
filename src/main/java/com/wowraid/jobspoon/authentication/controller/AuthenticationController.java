package com.wowraid.jobspoon.authentication.controller;

import com.wowraid.jobspoon.authentication.controller.response_form.TokenAuthenticationExpiredResponseForm;
import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<String> logout(
            @CookieValue(name = "userToken", required = false) String userToken,
            HttpServletResponse response) {
        log.info("로그아웃 호출");

        try {
            if (userToken == null || userToken.isEmpty()) {
                log.info("토큰 없음");
                return ResponseEntity.badRequest().body("fail: no token");
            }

            // 쿠키 삭제
            Cookie cookie = new Cookie("userToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            boolean logoutResult = authenticationService.logout(userToken);
            if (logoutResult) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail: invalid token");
            }
        } catch (Exception e) {
            log.error("로그아웃 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail: server error");
        }
    }


}
