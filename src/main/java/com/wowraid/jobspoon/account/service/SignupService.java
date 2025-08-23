package com.wowraid.jobspoon.account.service;

import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.service.register_response.RegisterResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SignupService {

    RegisterResponse signup(String AccessToken, RegisterRequestForm registerRequestForm);

}
