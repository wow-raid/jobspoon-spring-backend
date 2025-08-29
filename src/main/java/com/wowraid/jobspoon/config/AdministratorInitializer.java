package com.wowraid.jobspoon.config;

import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;
import com.wowraid.jobspoon.administer.service.AdministratorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdministratorInitializer {

    private final AdministratorService administratorService;
    private final AccountProfileService accountProfileService;
    private final AccountService accountService;
    @Value("${admin.first-admin-email:}")
    private String adminEmail;
    @Value("${admin.first-admin-nickname:}")
    private String adminNickname;
    @Value("${admin.first-admin-logintype:}")
    private LoginType adminLoginType;

    @PostConstruct
    public void initAdmin() {
        if (isBlank(adminEmail) || isBlank(adminNickname)) {
            log.info("[AdministratorInitializer] skipped: admin.* not set");
            return;
        }
        try {
            administratorService.createAdminIfNotExists(adminEmail, adminNickname, adminLoginType);
            log.info("[AdministratorInitializer] executed with {}", adminEmail);
        } catch (IllegalArgumentException e) {
            log.error("[AdministratorInitializer] invalid loginType: {}", adminLoginType, e);
        } catch (Exception e) {
            // PostConstruct에서 예외 throw 하면 부팅 자체가 실패할 수 있음
            log.error("[AdministratorInitializer] admin bootstrap failed", e);
        }
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
