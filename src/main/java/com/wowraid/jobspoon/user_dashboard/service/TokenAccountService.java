package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenAccountService {

    private final RedisCacheService redisCacheService;

    public Long resolveAccountId(String authorizationHeader){
        String token = extractToken(authorizationHeader);
        Long accountId = redisCacheService.getValueByKey(token, Long.class);

        if(accountId == null){
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
