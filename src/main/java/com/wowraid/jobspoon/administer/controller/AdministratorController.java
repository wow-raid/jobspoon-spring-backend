package com.wowraid.jobspoon.administer.controller;

import com.wowraid.jobspoon.administer.controller.dto.AdministratorCodeLoginRequest;
import com.wowraid.jobspoon.administer.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
