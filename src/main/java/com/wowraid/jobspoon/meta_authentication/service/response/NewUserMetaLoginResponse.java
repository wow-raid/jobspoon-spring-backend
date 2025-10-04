package com.wowraid.jobspoon.meta_authentication.service.response;


import lombok.Getter;

@Getter
public class NewUserMetaLoginResponse extends MetaLoginResponse {

    private final String htmlResponse;
    private final String userToken;


    public NewUserMetaLoginResponse(boolean isNewUser, String token, String nickname, String email, String origin) {
        this.userToken = token;
        this.htmlResponse = """
        <html><body><script>
        window.opener.postMessage({
            isNewUser: %s,
            accessToken: '%s',
            user: { nickname: '%s', email: '%s' }
        }, '%s'); window.close();
        </script></body></html>
        """.formatted(isNewUser, token, escape(nickname), escape(email), origin);
    }

    @Override
    public String getHtmlResponse() {
        return htmlResponse;
    }

    @Override
    public String getUserToken() {
        return userToken;
    }

}
