package com.wowraid.jobspoon.kakao_authentication.controller;

import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import com.wowraid.jobspoon.kakao_authentication.service.KakaoAuthenticationService;
import com.wowraid.jobspoon.kakao_authentication.service.mobile_response.KakaoLoginMobileResponse;
import com.wowraid.jobspoon.kakao_authentication.service.response.KakaoLoginResponse;
import com.wowraid.jobspoon.userAttendance.service.AttendanceService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/kakao-authentication")
@RequiredArgsConstructor
public class KakaoAuthenticationController {

    private final KakaoAuthenticationService kakaoAuthenticationService;
    private final AuthenticationService authenticationService;
    private final AttendanceService attendanceService;

    @GetMapping("/kakao/link")
    public String kakaoOauthLink() {
        return kakaoAuthenticationService.requestKakaoOauthLink();
    }

    @GetMapping("/login")
    public void kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        log.info("Kakao Login Request");

        try {
            KakaoLoginResponse kakaoLoginResponse = kakaoAuthenticationService.handleLogin(code);

            if(!kakaoLoginResponse.getIsNewUser()){
                String cookieHeader = String.format(
                        "userToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                        kakaoLoginResponse.getUserToken(),
//                    12 * 60 * 60
                        6 * 60 * 60// 6시간

                );        // CSRF 방어
                response.addHeader("Set-Cookie", cookieHeader);
                Long accountId = authenticationService.getAccountIdByUserToken(kakaoLoginResponse.getUserToken());
                boolean created = attendanceService.markLogin(accountId);


            }


            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(kakaoLoginResponse.getHtmlResponse());
        } catch (Exception e) {
            log.error("Kakao 로그인 에러", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("카카오 로그인 실패: " + e.getMessage());
        }
    }

    @GetMapping("/login/mobile")
    public ResponseEntity<KakaoLoginMobileResponse> kakaoLoginMobile(@RequestHeader("Authorization") String authenticationHeader) {
        String accessToken = authenticationHeader.replace("Bearer ", "").trim();

        try {
            KakaoLoginMobileResponse kakaoLoginMobileResponse = kakaoAuthenticationService.handleLoginMobile(accessToken);
            return  new ResponseEntity<>(kakaoLoginMobileResponse, HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            log.info("모바일 로그인 오류 발생 : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
