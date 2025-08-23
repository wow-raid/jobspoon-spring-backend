package com.wowraid.jobspoon.account.controller.request_form;

import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class RegisterRequestForm {

    private final String email;
    private final String nickname;
    private final LoginType loginType;
    private final String tempToken;


    public RegisterAccountRequest toRegisterAccountRequest() {
        return new RegisterAccountRequest(loginType);
    }

    public RegisterAccountProfileRequest toRegisterAccountProfileRequestForm() {
        return new RegisterAccountProfileRequest(nickname, email);
    }


}
