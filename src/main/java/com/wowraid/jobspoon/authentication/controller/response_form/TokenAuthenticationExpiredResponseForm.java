package com.wowraid.jobspoon.authentication.controller.response_form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class TokenAuthenticationExpiredResponseForm {

    private final boolean status;
    

}
