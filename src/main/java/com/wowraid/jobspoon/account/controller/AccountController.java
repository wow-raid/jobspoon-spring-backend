package com.wowraid.jobspoon.account.controller;

import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.account.service.SignupService;
import com.wowraid.jobspoon.account.service.register_response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final SignupService signupService;
    private final AccountService accountService;


    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signup(@CookieValue(name="userToken", required = false) String temporaryUserToken,
                                                   @RequestBody RegisterRequestForm registerRequestForm) {

        log.info("Signup request - 회원가입 호출 완료");

        RegisterResponse signupResult = signupService.signup(temporaryUserToken, registerRequestForm);

        return ResponseEntity.ok(signupResult);
    }



    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@CookieValue(name="userToken", required = false) String userToken){
        log.info("회원탈퇴 접근");

        try {
            accountService.withdraw(userToken);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            log.info("회원 탈퇴 요청에서 오류 발생 : {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }



}
