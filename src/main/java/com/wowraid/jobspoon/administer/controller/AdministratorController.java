package com.wowraid.jobspoon.administer.controller;

import com.wowraid.jobspoon.administer.controller.dto.AdministratorCodeLoginRequest;
import com.wowraid.jobspoon.administer.controller.dto.AdministratorUserInfoRequest;
import com.wowraid.jobspoon.administer.controller.dto.AdministratorUserInfoResponse;
import com.wowraid.jobspoon.administer.service.AdministratorManagementService;
import com.wowraid.jobspoon.administer.service.AdministratorService;
import com.wowraid.jobspoon.administer.service.dto.AdministratorUserListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/administrator")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;
    private final AdministratorManagementService administratorManagementService;
    @GetMapping("/temptoken_valid")
    public ResponseEntity<Void> validate(@RequestHeader("X-Temp-Admin-Token") String tempToken) {
        if(tempToken == null || tempToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean validresult=administratorService.isTempTokenValid(tempToken);
        return validresult
                ? ResponseEntity.ok().build()   // 200
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401
    }
    @PostMapping("/code_login")
    public ResponseEntity<Void> code_login(@RequestBody AdministratorCodeLoginRequest request
    ){
        boolean valid = administratorService.validateKey(request.getAdministratorId(), request.getAdministratorpassword());
        if (valid) {
            String temporaryAdminToken = administratorService.createTemporaryAdminToken();
            log.info("temporaryAdminToken:{}",temporaryAdminToken);
            return ResponseEntity
                    .ok()
                    .header("Authorization", "Bearer " + temporaryAdminToken)
                    .header("Access-control-Expose-Headers", "Authorization")
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
//        return valid
//                ? ResponseEntity.ok().build() //200처리
//                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 처리
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
