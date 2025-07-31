package com.wowraid.jobspoon.account.controller.request_form;

public class UserTokenRequest {
    private String userToken;

    public UserTokenRequest(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}