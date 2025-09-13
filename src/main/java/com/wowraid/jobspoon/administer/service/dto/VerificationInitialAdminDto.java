package com.wowraid.jobspoon.administer.service.dto;

import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.entity.RoleType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class VerificationInitialAdminDto {
    private final Long adminAccountId;
    private final LoginType adminLoginType;
    private final RoleType adminRoleType;

}
