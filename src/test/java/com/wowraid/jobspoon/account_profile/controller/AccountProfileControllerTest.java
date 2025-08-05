package com.wowraid.jobspoon.account_profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.account_profile.service.AccountProfileService;
import com.wowraid.jobspoon.account_profile.service.TokenAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AccountProfileController.class)
public class AccountProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountProfileService accountProfileService;

    @MockBean
    private TokenAccountService tokenAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적인 userToken으로 AccountProfile 조회 성공")
    void requestInfo_success() throws Exception{
        //given
        String userToken = "valid-token";
        Long accountId = 1L;

        given(tokenAccountService.resolveAccountId(userToken)).willReturn(accountId);
        given(accountProfileService.findEmail(accountId)).willReturn("test@email.com");
        given(accountProfileService.findNickname(accountId)).willReturn("곰돌이");
        given(accountProfileService.findGender(accountId)).willReturn("FEMALE");
        given(accountProfileService.findBirthyear(accountId)).willReturn("1998");

        Map<String, String> request = Map.of("userToken", userToken);

        //when + then
        mockMvc.perform(post("/account-profile/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.nickname").value("곰돌이"))
                .andExpect(jsonPath("$.gender").value("FEMALE"))
                .andExpect(jsonPath("$.birthyear").value("1998"));
    }

    @Test
    @DisplayName("유효하지 않은 userToken으로 잘못된 요청 처리")
    void requestInfo_invalidToken() throws Exception{
        //given
        String userToken = "invalid-token";
        given(tokenAccountService.resolveAccountId(userToken)).willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."));
        Map<String, String> request = Map.of("userToken", userToken);

        //when + then
        mockMvc.perform(post("/account-profile/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("서비스 내부 예외 발생 시 서버 오류 반환")
    void requestInfo_internalError() throws Exception{
        //given
        String userToken = "error-token";
        given(tokenAccountService.resolveAccountId(userToken)).willReturn(1L);
        given(accountProfileService.findEmail(anyLong())).willThrow(new RuntimeException("DB 연결 오류"));

        Map<String, String> request = Map.of("userToken", userToken);

        //when + then
        mockMvc.perform(post("/account-profile/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("서버 오류가 발생했습니다"));
    }
}
