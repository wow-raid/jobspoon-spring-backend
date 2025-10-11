package com.wowraid.jobspoon.userDashboard.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenAccountService {

    private final RedisCacheService redisCacheService;

    public Long resolveAccountId(String token){
        log.info("🔑 Cookie 토큰 수신: {}", token);

        if(token == null || token.isEmpty()){
            throw new IllegalArgumentException("쿠키에 userToken이 없습니다.");
        }

        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        log.info("✅ Redis 조회 결과: accountId = {}", accountId);

        if(accountId == null){
            log.warn("❌ 유효하지 않은 토큰이거나 만료됨: {}", token);
            throw new IllegalArgumentException("유효하지 않은 토큰이거나 만료되었습니다.");
        }

        return accountId;
    }
}
