package com.wowraid.jobspoon.github_authentication.controller;

import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;
import com.wowraid.jobspoon.github_authentication.service.GithubAuthenticationService;
import com.wowraid.jobspoon.github_authentication.service.response.GithubLoginResponse;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/github-authentication")
@RequiredArgsConstructor
public class GithubAuthenticationController {
    final private GithubAuthenticationService githubAuthenticationService;
    final private AccountService accountService;
    final private AccountProfileService accountProfileService;
    final private RedisCacheService redisCacheService;
    final private LoginType GithubType =LoginType.GITHUB;

    @GetMapping("/request-login-url")
    public String requestGetLoginLink() {
        log.info("requestGetLoginLink() called");
        String loginLink = githubAuthenticationService.getLoginLink();
        if(loginLink == null){
            log.info("loginLink is null");
            return null;
        }
        return loginLink;
    }

    @GetMapping("/login")
    @Transactional
    public void GithubLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        log.info("requestAccessToken(): code {}", code);
        try {
            GithubLoginResponse githubLoginResponse = githubAuthenticationService.handleLogin(code);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(githubLoginResponse.getHtmlResponse());
        } catch (Exception e) {
            log.error("Github 로그인 에러", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("깃허브 로그인 실패: " + e.getMessage());
        }
    }

//
//    @GetMapping("/userinfo")
//    public ResponseEntity<UserInfoDto> getUserInfo(@RequestParam("token") String userToken) {
//        // 1) Redis 에서 accountId 조회
//        Long accountId = redisCacheService.getValueByKey(userToken);
//        if (accountId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
//                    body(new UserInfoDto(false, null, null, "Invalid token"));
//        }
//
//        // 2) DB 조회로 신규/기존 회원 구분
//        // 현재 Email로 처리되니까 email로 시도해보자
//        boolean isNew = accountProfileService.loadProfileByAccountId(accountId).isEmpty();
//        String email = String.valueOf(redisCacheService.getValueByKey(userToken + ":email"));
//        String nickname = String.valueOf(redisCacheService.getValueByKey(userToken + ":nickname"));
//        UserInfoDto dto = new UserInfoDto(isNew, email, nickname, null);
//        return ResponseEntity.ok(dto);
//    }
//
//    @PostMapping("/accept-terms")
//    public ResponseEntity<Void> acceptTerms(@RequestParam("token") String userToken,
//                                            @RequestBody TermsAcceptRequest req) {
//        String accountId = String.valueOf(redisCacheService.getValueByKey(userToken));
//        if (accountId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        // 서비스에서 Profile 의 termsAccepted 필드 업데이트
//        accountProfileService.markTermsAccepted(
//                Long.valueOf(accountId),
//                req.isAgreed()
//        );
//        return ResponseEntity.ok().build();
//    }
//
//    public static record UserInfoDto(
//            boolean isNew,
//            String email,
//            String nickname,
//            String errorMessage
//    ) {
//    }
//
//    public static class TermsAcceptRequest {
//        // 1) 약관 동의 여부를 담는 필드
//        private boolean agreed;
//        // 2) 기본 생성자 (Jackson 등에서 리플렉션으로 객체 생성 시 필요)
//        public TermsAcceptRequest() {
//        }
//        // 3) 전체 생성자 (테스트 코드나 수동 객체 생성 시 유용)
//        public TermsAcceptRequest(boolean agreed) {
//            this.agreed = agreed;
//        }
//        // 4) getter: JSON 바인딩 시에도 isAgreed()가 호출되어 조회됨
//        public boolean isAgreed() {
//            return agreed;
//        }
//        // 5) setter: 요청 JSON을 바탕으로 필드에 값이 세팅됨
//        public void setAgreed(boolean agreed) {
//            this.agreed = agreed;
//        }
//    }
}