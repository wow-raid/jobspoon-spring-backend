package com.wowraid.jobspoon.account.service.register_request;


import com.wowraid.jobspoon.account.entity.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterAccountRequest {

    private final LoginType loginType;


}
