package com.wowraid.jobspoon.github_authentication.service.response;


public class ExistingAdminGithubLoginResponse extends GithubLoginResponse {

    private final String htmlResponse;

    public ExistingAdminGithubLoginResponse(boolean isNewUser, String token, String nickname, String email) {
        this.htmlResponse = """
        <html><body><script>
        window.opener.postMessage({
            isNewUser: %s,
            userToken: '%s',
            user: { nickname: '%s', email: '%s' }
        }); window.close();
        </script></body></html>
        """.formatted(isNewUser, token, escape(nickname), escape(email));
    }

    @Override
    public String getHtmlResponse() {
        return htmlResponse;
    }
}
