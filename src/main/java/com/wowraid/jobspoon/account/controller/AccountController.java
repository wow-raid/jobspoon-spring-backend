package com.wowraid.jobspoon.account.controller;

import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
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


    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signup(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestBody RegisterRequestForm registerRequestForm) {

        log.info("Signup request - 회원가입 호출 완료");
        log.info("RegisterRequestForm1 :  {}", registerRequestForm.getLoginType());
        log.info("RegisterRequestForm2 :  {}", registerRequestForm.getNickname());
        String temporaryUserToken = authorizationHeader.replace("Bearer ", "").trim();
        RegisterResponse signupResult = signupService.signup(temporaryUserToken, registerRequestForm);

        return ResponseEntity.ok(signupResult);

    }



}
