package com.wowraid.jobspoon.user_trustscore.controller;

import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import com.wowraid.jobspoon.user_trustscore.controller.response.TrustScoreHistoryResponse;
import com.wowraid.jobspoon.user_trustscore.controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.user_trustscore.service.TrustScoreHistoryService;
import com.wowraid.jobspoon.user_trustscore.service.TrustScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trust-score")
@RequiredArgsConstructor
public class UserTrustScoreController {

    private final TokenAccountService tokenAccountService;
    private final TrustScoreService trustScoreService;
    private final TrustScoreHistoryService trustScoreHistoryService;

    /**
     * 최신 신뢰점수 조회
     */
    @GetMapping
    public ResponseEntity<TrustScoreResponse> getTrustScore(
            @CookieValue(name = "userToken", required = false) String userToken
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(trustScoreService.getTrustScore(accountId));
    }

    /**
     * 최신 신뢰점수 갱신 (계산 후 DB 저장)
     */
    @PostMapping("/calculate")
    public ResponseEntity<TrustScoreResponse> calculateTrustScore(
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(trustScoreService.calculateTrustScore(accountId));
    }

    /**
     * 월별 신뢰점수 히스토리 조회
     */
    @GetMapping("/history")
    public ResponseEntity<List<TrustScoreHistoryResponse>> getTrustScoreHistory(
            @CookieValue(name = "userToken", required = false) String userToken
    ){
        long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(trustScoreHistoryService.getHistory(accountId));
    }
}
