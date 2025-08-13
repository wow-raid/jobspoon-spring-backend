package com.wowraid.jobspoon.config;


import com.wowraid.jobspoon.account.entity.AccountLoginType;
import com.wowraid.jobspoon.account.entity.AccountRoleType;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.entity.RoleType;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class DBInitializer {

    private final AccountRoleTypeRepository accountRoleTypeRepository;
    private final AccountLoginTypeRepository accountLoginTypeRepository;

    @PostConstruct
    private void init () {
        log.debug("initializer 시작!");

        initAccountRoleTypes();
        initAccountLoginTypes();

        log.debug("initializer 종료!");
    }

    private void initAccountRoleTypes() {
        try {
            final Set<RoleType> roles =
                    accountRoleTypeRepository.findAll().stream()
                            .map(AccountRoleType::getRoleType)
                            .collect(Collectors.toSet());

            for (RoleType type: RoleType.values()) {
                if (!roles.contains(type)) {
                    final AccountRoleType role = new AccountRoleType(type);
                    accountRoleTypeRepository.save(role);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void initAccountLoginTypes() {
        try {
            final Set<LoginType> loginTypesInDB =
                    accountLoginTypeRepository.findAll().stream()
                            .map(AccountLoginType::getLoginType)
                            .collect(Collectors.toSet());

            for (LoginType type : LoginType.values()) {
                if (!loginTypesInDB.contains(type)) {
                    final AccountLoginType loginType = new AccountLoginType(type);
                    accountLoginTypeRepository.save(loginType);
                }
            }
        } catch (Exception e) {
            log.error("LoginType 초기화 실패", e);
        }
    }
}

