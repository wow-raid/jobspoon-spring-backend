package com.wowraid.jobspoon.github_authentication.service.response;

public abstract class GithubLoginResponse {
    public static GithubLoginResponse of(boolean isNewUser, String token, String nickname, String email) {
        return isNewUser
                ? new NewAdminGithubLoginResponse(isNewUser, token, nickname, email)
                : new ExistingAdminGithubLoginResponse(isNewUser, token, nickname, email);
    }
    public abstract String getHtmlResponse();
    protected static String escape(String str) {
        return str.replace("'", "\\'");
    }

}

