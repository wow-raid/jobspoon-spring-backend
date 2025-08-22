package com.wowraid.jobspoon.account.controller;

import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.service.SignupService;
import com.wowraid.jobspoon.account.service.register_response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final SignupService signupService;


    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signup(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestBody RegisterRequestForm registerRequestForm) {

        RegisterResponse signupResult = signupService.signup(authorizationHeader, registerRequestForm);

        return ResponseEntity.ok(signupResult);

    }


}
