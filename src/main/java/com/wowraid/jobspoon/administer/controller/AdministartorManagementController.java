package com.wowraid.jobspoon.administer.controller;

import com.wowraid.jobspoon.administer.controller.dto.AdministratorUserInfoRequest;
import com.wowraid.jobspoon.administer.service.AdministratorManagementService;
import com.wowraid.jobspoon.administer.service.AdministratorService;
import com.wowraid.jobspoon.administer.service.dto.AdministratorUserListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/administrator/management")
public class AdministartorManagementController {
    private final AdministratorService administratorService;
    private final AdministratorManagementService administratorManagementService;

    @PostMapping("/userinfo")
    public ResponseEntity<AdministratorUserListResponse> getUserInfo(@RequestHeader("Authorization")  String userToken,
                                                                     @RequestBody AdministratorUserInfoRequest request){
        log.info("AdministratorController.getUserInfo is working");
        boolean valid = administratorService.isAdminByUserToken(userToken.replace("Bearer ", "").trim());
        if(!valid){
            log.info("valid is not matched");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AdministratorUserListResponse administratorUserListResponse = administratorManagementService.getUserInfo(request);

        return ResponseEntity.ok(administratorUserListResponse);
    }
}
