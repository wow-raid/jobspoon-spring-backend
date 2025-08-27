package com.wowraid.jobspoon.account.service;


import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.entity.LoginType;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
import com.wowraid.jobspoon.account.service.register_response.RegisterResponse;
import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.entity.request.RegisterAccountProfileRequest;
import com.wowraid.jobspoon.accountProfile.service.AccountProfileService;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class SignupServiceTest {

    @InjectMocks
    private SignupServiceImpl signupService;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountProfileService accountProfileService;

    @Mock
    private RedisCacheService redisCacheService;






    @Nested
    class Signup {


        @Test
        void 회원가입_성공(){

            String email = "email";
            String nickname = "nickname";
            String AccessToken = "accessToken";
            String tempToken = "tempToken";

            Account account = new Account();
            RegisterRequestForm registerRequestForm =
                    new RegisterRequestForm(email, nickname, LoginType.KAKAO, tempToken);

            AccountProfile accountProfile = new AccountProfile(account, nickname, email);

            // given
            given(accountService.createAccount(any(RegisterAccountRequest.class))).willReturn(Optional.of(account));
            given(accountProfileService.createAccountProfile(any(Account.class), any(RegisterAccountProfileRequest.class))).willReturn(Optional.of(accountProfile));


            // when
            RegisterResponse registerResponse = signupService.signup(tempToken, registerRequestForm);


            // then
            Assertions.assertNotNull(registerResponse);
            Assertions.assertEquals(nickname, registerResponse.getNickname());
            Assertions.assertEquals(email, registerResponse.getEmail());




        }





    }


}
