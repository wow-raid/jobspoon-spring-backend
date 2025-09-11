package com.wowraid.jobspoon.authentication.service;

public interface AuthenticationService {

    String createUserTokenWithAccessToken(Long accountId, String accessToken);
    String createTemporaryUserTokenWithAccessToken(String accessToken);
    boolean deleteToken(String userToken);
    boolean logout(String userToken);


}
