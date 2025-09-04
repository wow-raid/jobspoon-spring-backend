package com.wowraid.jobspoon.administer.service;


import com.wowraid.jobspoon.account.entity.LoginType;

public interface AdministratorService {
    boolean validateKey(String id,String password);
    void createAdminIfNotExists(String adminEmail, String adminNickname, LoginType adminLoginType);
    boolean isAdminByUserToken(String userToken);
    String createTemporaryAdminToken();
}
