package com.wowraid.jobspoon.kakao_authentication.service.response;


import lombok.Getter;

@Getter
public class NewUserKakaoLoginResponse extends KakaoLoginResponse {

    private final String htmlResponse;

    public NewUserKakaoLoginResponse(boolean isNewUser, String token, String nickname, String email, String origin) {
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

}
