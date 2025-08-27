package com.wowraid.jobspoon.administer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdministerServiceImpl implements AdministerService {

    @Value("${admin.secret-id-key}")
    private String secretIdKey;
    @Value("${admin.secret-password-key}")
    private String secretPasswordKey;

    @Override
    public boolean validateKey(String id, String password) {
        return secretIdKey.equals(id) && secretPasswordKey.equals(password);
    }
}
