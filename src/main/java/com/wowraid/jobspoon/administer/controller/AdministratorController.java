package com.wowraid.jobspoon.administer.controller;

import com.wowraid.jobspoon.administer.controller.dto.AdministratorCodeLoginRequest;
import com.wowraid.jobspoon.administer.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/administer")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @PostMapping("/code_login")
    public ResponseEntity<Void> code_login(@RequestBody AdministratorCodeLoginRequest request
    ){
        boolean valid = administratorService.validateKey(request.getAdministerId(), request.getAdministerpassword());
        return valid
                ? ResponseEntity.ok().build() //200처리
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 처리
    }
    @PostMapping("/social_login")
    public ResponseEntity<Void> social_login(@RequestHeader("Authorization")  String userToken){
        String temporaryUserToken = userToken.replace("Bearer ", "").trim();
//        log.info("[AdministratorController] social_login userToken: {}", temporaryUserToken);
        boolean valid = administratorService.isAdminByUserToken(temporaryUserToken);
//        log.info("[AdministratorController] social_login valid: {}", valid);
        return valid
                ? ResponseEntity.ok().build() //200처리
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 처리
    }

}
