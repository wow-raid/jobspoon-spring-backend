package com.wowraid.jobspoon.account.controller.request_form;

public class UserTokenRequestForm {
    private String userToken;

    public UserTokenRequestForm(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}