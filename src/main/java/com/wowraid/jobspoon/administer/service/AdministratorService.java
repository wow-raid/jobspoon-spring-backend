package com.wowraid.jobspoon.administer.service;


import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.entity.RoleType;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;

import java.util.Optional;

public interface AdministratorService {
    boolean validateKey(String id,String password);
    void createAdminIfNotExists(String adminEmail, String adminNickname, LoginType adminLoginType);
    boolean isAdminByUserToken(String userToken);
    String createTemporaryAdminToken();
    boolean isTempTokenValid(String tempToken);
}
