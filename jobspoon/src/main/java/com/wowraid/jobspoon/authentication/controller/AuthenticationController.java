package com.wowraid.jobspoon.authentication.controller;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final RedisCacheService redisService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String userToken = body.get("userToken");

        if (userToken == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "userToken이 필요합니다"));
        }

        try {
            // userToken → accountId 조회
            String accountId = redisService.getValueByKey(userToken, String.class);

            // Redis에서 양쪽 키 삭제
            redisService.deleteByKey(userToken);       // userToken → accountId
            redisService.deleteByKey(accountId);       // accountId → accessToken

            return ResponseEntity.ok(Map.of("message", "로그 아웃 성공"));
        } catch (Exception e) {
            log.error("Redis 삭제 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "코드 내부 에러"));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
        String userToken = body.get("userToken");

        if (userToken == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("valid", false, "error", "userToken이 필요합니다"));
        }

        try {
            String accountId = redisService.getValueByKey(userToken, String.class);
            boolean isValid = (accountId != null);
            return ResponseEntity.ok(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("valid", false, "error", "코드 내부 에러"));
        }
    }
}
