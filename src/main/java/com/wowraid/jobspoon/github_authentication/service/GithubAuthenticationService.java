package com.wowraid.jobspoon.github_authentication.service;

import com.wowraid.jobspoon.github_authentication.service.response.GithubLoginResponse;

import java.util.Map;

public interface GithubAuthenticationService {
    String getLoginLink();
    Map<String, Object> requestAccessToken(String code);
    Map<String, Object> requestUserInfo(String accessToken);
    String requestPrimaryEmail(String accessToken);
    GithubLoginResponse handleLogin(String code);
}