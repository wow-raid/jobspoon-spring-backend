package com.wowraid.jobspoon.account.controller;

import com.wowraid.jobspoon.account.controller.request_form.UserTokenRequestForm;
import com.wowraid.jobspoon.account.controller.response_form.ApiResponse;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final RedisCacheService redisCacheService;

    /** 사용자 이메일 요청 */
    @PostMapping("/email")
    public ResponseEntity<?> requestEmail(@RequestBody UserTokenRequestForm request) {
        return withAccountId(request.getUserToken(), accountId -> {
            String email = accountService.findEmail(accountId);
            return (email != null)
                    ? ApiResponse.ok("email", email)
                    : ApiResponse.fail("이메일을 찾을 수 없습니다", 404);
        });
    }

    /** 사용자 탈퇴 요청 */
    @PostMapping("/withdraw")
    public ResponseEntity<?> requestWithdraw(@RequestBody UserTokenRequestForm request) {
        return withAccountId(request.getUserToken(), accountId -> {
            String accountIdStr = accountId.toString();
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

            accountService.createWithdrawAccount(accountIdStr);
            accountService.createWithdrawAt(accountIdStr, now);
            accountService.createWithdrawEnd(accountIdStr, now);

            boolean success = accountService.withdraw(accountIdStr);
            if (!success) return ApiResponse.fail("회원 탈퇴에 실패했습니다");

            redisCacheService.deleteByKey(request.getUserToken());
            redisCacheService.deleteByKey(accountIdStr);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("accountId", accountId);
            responseMap.put("withdraw_at", now);
            responseMap.put("withdraw_end", now.plusYears(3));

            return ApiResponse.ok(responseMap);
        });
    }

    /** 공통 accountId 처리 핸들러 */
    private ResponseEntity<?> withAccountId(String token, TokenHandler handler) {
        if (token == null || token.isBlank()) {
            return ApiResponse.fail("userToken이 필요합니다");
        }

        try {
            Long accountId = redisCacheService.getValueByKey(token, Long.class);
            if (accountId == null) {
                return ApiResponse.fail("유효한 userToken이 아닙니다", 404);
            }
            return handler.handle(accountId);

        } catch (NumberFormatException e) {
            return ApiResponse.fail("accountId 형식 오류", 400);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("지원되지 않는 Redis 타입", 500);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail("서버 내부 오류", 500);
        }
    }

    @FunctionalInterface
    private interface TokenHandler {
        ResponseEntity<?> handle(Long accountId);
    }
}
