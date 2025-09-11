package com.wowraid.jobspoon.authentication.controller;

import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization")String authorizationHeader) {
        log.info("로그아웃 호출");
        String userToken = authorizationHeader.replace("Bearer ", "").trim();
        boolean logoutResult = authenticationService.logout(userToken);
        if (logoutResult) {
            return "success";
        }else{
            return "fail";
        }

    }


}
