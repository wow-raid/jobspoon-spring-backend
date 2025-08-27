package com.wowraid.jobspoon.administer.controller;

import com.wowraid.jobspoon.administer.controller.dto.AdministerCodeLoginRequest;
import com.wowraid.jobspoon.administer.service.AdministerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administer")
@RequiredArgsConstructor
public class AdministerController {

    private final AdministerService administerService;

    @PostMapping("/code_login")
    public ResponseEntity<Void> code_login(@RequestBody AdministerCodeLoginRequest request
    ){
        boolean valid = administerService.validateKey(request.getAdministerId(), request.getAdministerpassword());
        return valid
                ? ResponseEntity.ok().build() //200처리
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 처리
    }

}
