package com.wowraid.jobspoon.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisCacheService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /auth/logout")
    class Logout {

        @Test
        @DisplayName("성공: userToken 유효 -> Redis 삭제 완료")
        void logout_success() throws Exception {
            String userToken = "token-123";
            String accountId = "1";

            given(redisService.getValueByKey(userToken, String.class)).willReturn(accountId);
            willDoNothing().given(redisService).deleteByKey(userToken);
            willDoNothing().given(redisService).deleteByKey(accountId);

            mockMvc.perform(post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userToken", userToken))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("로그 아웃 성공"));
        }

        @Test
        @DisplayName("실패: userToken 없음")
        void logout_missingToken() throws Exception {
            mockMvc.perform(post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("userToken이 필요합니다"));
        }
    }

    @Nested
    @DisplayName("POST /auth/validate-token")
    class ValidateToken {

        @Test
        @DisplayName("성공: userToken 존재 -> valid: true")
        void validateToken_success() throws Exception {
            String userToken = "token-123";

            given(redisService.getValueByKey(userToken, String.class)).willReturn("1");

            mockMvc.perform(post("/auth/validate-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("userToken", userToken))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        @DisplayName("성공: userToken 없음 -> valid: false")
        void validateToken_invalid() throws Exception {
            String userToken = "token-123";

            given(redisService.getValueByKey(userToken, String.class)).willReturn(null);

            mockMvc.perform(post("/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("userToken", userToken))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        @DisplayName("실패: userToken 요청 누락")
        void validateToken_missingToken() throws Exception {
            mockMvc.perform(post("/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.valid").value(false))
                    .andExpect(jsonPath("$.error").value("userToken이 필요합니다"));
        }
    }
}
