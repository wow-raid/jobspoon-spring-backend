package com.wowraid.jobspoon.interview.controller;

import com.wowraid.jobspoon.interview.controller.response_form.UserTechStackResponse;
import com.wowraid.jobspoon.interview.service.UserTechStackService;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class UserTechStackController {

    private final RedisCacheService redisCacheService;
    private final UserTechStackService userTechStackService;

    @GetMapping("/my-techstack")
    public ResponseEntity<UserTechStackResponse> getUserTechStack(
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        return ResponseEntity.ok(userTechStackService.getUserTechStack(accountId));
    }
}
