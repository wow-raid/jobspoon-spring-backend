package com.wowraid.jobspoon.account_profile.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * userToken을 기반으로 accountId를 Redis에서 추출해주는 유틸성 서비스
 */

@Service
@RequiredArgsConstructor
public class TokenAccountService {

    private final RedisCacheService redisCacheService;

    public Long resolveAccountId(String userToken) {
        if (userToken == null || userToken.isBlank()) {
            throw new IllegalArgumentException("userToken이 필요합니다");
        }

        String accountIdStr = redisCacheService.getValueByKey(userToken, String.class);
        if (accountIdStr == null) {
            throw new IllegalArgumentException("유효하지 않은 userToken입니다");
        }

        return Long.valueOf(accountIdStr);
    }
}
