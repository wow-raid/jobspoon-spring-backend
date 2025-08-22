package com.wowraid.jobspoon.account.service;


import com.wowraid.jobspoon.account.controller.request_form.RegisterRequestForm;
import com.wowraid.jobspoon.account.entity.*;
import com.wowraid.jobspoon.account.repository.AccountLoginTypeRepository;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.account.repository.AccountRoleTypeRepository;
import com.wowraid.jobspoon.account.service.register_request.RegisterAccountRequest;
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
public class AccountServiceTset {


    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountRoleTypeRepository accountRoleTypeRepository;

    @Mock
    private AccountLoginTypeRepository accountLoginTypeRepository;


    @Nested
    class createAccount {
        String email = "test_email";
        String nickname = "test_nickname";
        LoginType loginType = LoginType.KAKAO;
        String tmepToken = "tmepToken";

        RegisterRequestForm requestForm = new RegisterRequestForm(
                email,
                nickname,
                loginType,
                tmepToken
        );

        RegisterAccountRequest request = requestForm.toRegisterAccountRequest();



        @Test
        void RoleType_USER가_DB에_세팅_되어있지_않을_경우_IllegalStateException_예외가_발생합니다(){
            // given
            given(accountRoleTypeRepository.findByRoleType(RoleType.USER)).willReturn(Optional.empty());

            // when
            // then

            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                accountService.createAccount(request);
            });

        }

        @Test
        void RoleType_USER가_DB에_세팅_되어있는_경우_예외가_발생하지_않습니다(){
            // given
            given(accountRoleTypeRepository.findByRoleType(RoleType.USER)).willReturn(Optional.of(new AccountRoleType()));
            LoginType loginType = request.getLoginType();
            given(accountLoginTypeRepository.findByLoginType(loginType)).willReturn(Optional.of(new AccountLoginType()));
            // when
            // then

            Assertions.assertDoesNotThrow(() -> {
                accountService.createAccount(request);
            });

        }

        @Test
        void LoginType이_DB에_세팅_되어있지_않을_경우_IllegalStateException_예외가_발생합니다() {

            // given
            given(accountRoleTypeRepository.findByRoleType(RoleType.USER)).willReturn(Optional.of(new AccountRoleType()));
            LoginType loginType = request.getLoginType();
            given(accountLoginTypeRepository.findByLoginType(loginType)).willReturn(Optional.empty());

            // when
            // then
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                accountService.createAccount(request);
            });
        }

        @Test
        void LoginType이_DB에_세팅_되어있는_경우_예외가_발생하지_않습니다(){
            // given
            given(accountRoleTypeRepository.findByRoleType(RoleType.USER)).willReturn(Optional.of(new AccountRoleType()));
            LoginType loginType = request.getLoginType();
            given(accountLoginTypeRepository.findByLoginType(loginType)).willReturn(Optional.of(new AccountLoginType()));

            // when
            // then

            Assertions.assertDoesNotThrow(() -> {
                accountService.createAccount(request);
            });

        }

        @Test
        void account_생성_성공(){

            // given

            given(accountLoginTypeRepository.findByLoginType(loginType)).willReturn(Optional.of(new AccountLoginType(loginType)));
            given(accountRoleTypeRepository.findByRoleType(RoleType.USER)).willReturn(Optional.of(new AccountRoleType(RoleType.USER)));


            // when
            Account account = accountService.createAccount(request).get();
            // then
            Assertions.assertNotNull(account);
            Assertions.assertEquals(account.getAccountLoginType().getLoginType(), loginType);
            Assertions.assertEquals(RoleType.USER, account.getAccountRoleType().getRoleType());
        }



    }



}
