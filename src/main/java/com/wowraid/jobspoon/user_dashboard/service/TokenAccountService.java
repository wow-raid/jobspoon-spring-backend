package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenAccountService {

    private final RedisCacheService redisCacheService;

    public Long resolveAccountId(String authorizationHeader){
        log.info("🔑 Authorization header 수신: {}", authorizationHeader);

        String token = extractToken(authorizationHeader);
        log.info("📌 Extracted token: {}", token);

        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        log.info("✅ Redis 조회 결과: accountId = {}", accountId);

        if(accountId == null){
            log.warn("❌ 유효하지 않은 토큰이거나 만료됨: {}", token);
            throw new IllegalArgumentException("유효하지 않은 토큰이거나 만료되었습니다.");
        }

        return accountId;
    }

    private String extractToken(String authorizationHeader){
        if(authorizationHeader == null || authorizationHeader.isEmpty()){
            throw new IllegalArgumentException("Authorization header가 없습니다.");
        }

        String h = authorizationHeader.trim();

        if(h.regionMatches(true, 0, "Bearer ", 0, 7)){
            String token = h.substring(7).trim();

            if(token.isEmpty()){
                throw new IllegalArgumentException("Bearer 접두사 뒤에 토큰이 없습니다.");
            }
            return token;
        }

        if(h.isEmpty()){
            throw new IllegalArgumentException("토큰이 비어있습니다.");
        }
        return h;
    }
}
