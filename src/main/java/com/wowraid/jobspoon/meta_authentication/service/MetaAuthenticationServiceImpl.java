package com.wowraid.jobspoon.meta_authentication.service;


import com.wowraid.jobspoon.authentication.service.AuthenticationService;
import com.wowraid.jobspoon.config.FrontendConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class MetaAuthenticationServiceImpl implements MetaAuthenticationService {

    private final String loginUrl;
    private final String clientId;
    private final String redirectUri;
    private final String tokenRequestUri;
    private final String userInfoRequestUri;
    private final RestTemplate restTemplate;
    private final FrontendConfig frontendConfig;
    private final AuthenticationService authenticationService;


    public MetaAuthenticationServiceImpl(
        @Value("${meta.login-url}") String loginUrl,
        @Value("${meta.client-id}") String clientId,
        @Value("${meta.redirect-uri}") String redirectUri,
        @Value("${meta.token-request-uri}") String tokenRequestUri,
        @Value("${meta.user-info-request-uri}") String userInfoRequestUri,
        RestTemplate restTemplate,
        FrontendConfig frontendConfig,
        AuthenticationService authenticationService) {

            this.loginUrl = loginUrl;
            this.clientId = clientId;
            this.redirectUri = redirectUri;
            this.tokenRequestUri = tokenRequestUri;
            this.userInfoRequestUri = userInfoRequestUri;

            this.restTemplate = restTemplate;
            this.frontendConfig = frontendConfig;
            this.authenticationService = authenticationService;


    }



    @Override
    public String requestKakaoOauthLink() {
        log.info("Meta Link 요청 서비스 진입");

        return String.format("%s?client_id=%s&redirect_uri=%s&scope=catalog_management,public_profile&response_type=code", loginUrl, clientId, redirectUri);
    }
}
