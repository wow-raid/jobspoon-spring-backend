package com.wowraid.jobspoon.profile_appearance.Controller;

import com.wowraid.jobspoon.profile_appearance.Controller.request.AddExpRequest;
import com.wowraid.jobspoon.profile_appearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Controller.response.TrustScoreResponse;
import com.wowraid.jobspoon.profile_appearance.Controller.response.UserLevelResponse;
import com.wowraid.jobspoon.profile_appearance.Service.*;
import com.wowraid.jobspoon.user_dashboard.service.TokenAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile-appearance")
@RequiredArgsConstructor
public class ProfileAppearanceController {

    private final ProfileAppearanceService appearanceService;
    private final TokenAccountService tokenAccountService;
    private final TitleService titleService;
    private final TrustScoreService trustScoreService;
    private final UserLevelService userLevelService;

    /**
     * 내 프로필 외형 정보 조회
     * (사진, 별명, 장착된 칭호/랭크 등)
     */
    @GetMapping("/my")
    public ResponseEntity<AppearanceResponse> getMyAppearance(@CookieValue(name = "userToken", required = false) String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AppearanceResponse response = appearanceService.getMyAppearance(accountId);

        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 사진 업로드 (업로드 Presigned URL 발급)
     */
    @PostMapping("/profile/photo/upload-url")
    public ResponseEntity<String> getUploadUrl(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestParam("filename") String filename,
            @RequestParam("contentType") String contentType
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(
                appearanceService.generateUploadUrl(accountId, filename, contentType)
        );
    }

    /**
     * 프로필 사진 다운로드 (다운로드 Presigned URL 발급)
     */
    @GetMapping("/profile/photo/download-url")
    public ResponseEntity<String> getDownloadUrl(
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(
                appearanceService.generateDownloadUrl(accountId)
        );
    }

    /**
     * 특정 칭호 장착
     */
    @PutMapping("title/{titleId}/equip")
    public ResponseEntity<AppearanceResponse.Title> equipTitle(
            @CookieValue(name = "userToken", required = false) String userToken,
            @PathVariable Long titleId
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(titleService.equipTitle(accountId, titleId));
    }

    /**
     * 특정 칭호 장착 해제
     */
    @PutMapping("title/unequip")
    public ResponseEntity<Void> unequipTitle(
            @CookieValue(name = "userToken", required = false) String userToken
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        titleService.unequipTitle(accountId); //equippedTitle = null
        return ResponseEntity.ok().build();
    }

    /**
     * 내가 보유한 칭호 목록 조회
     */
    @GetMapping("/title/my")
    public ResponseEntity<List<AppearanceResponse.Title>> getMyTitles(
            @CookieValue(name = "userToken", required = false) String userToken
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(titleService.getMyTitles(accountId));
    }

    /**
     * 신뢰점수 계산 및 조회
     * (출석/문제풀이/댓글/게시글/스터디룸 등 지표 기반)
     */
    @GetMapping("/trust-score")
    public ResponseEntity<TrustScoreResponse> getTrustScore(
            @CookieValue(name = "userToken", required = false) String userToken
    ){
      Long accountId = tokenAccountService.resolveAccountId(userToken);
      return ResponseEntity.ok(trustScoreService.calculateTrustScore(accountId));
    }

    /**
     * 사용자 레벨 조회
     */
    @GetMapping("/user-level")
    public ResponseEntity<UserLevelResponse> getUserLevel(
            @CookieValue(name = "userToken", required = false) String userToken
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(userLevelService.getUserLevel(accountId));
    }

    /**
     * 경험치 추가 (레벨업 조건은 UserLevelService에서 처리)
     */
    @PostMapping("/user-level/experience")
    public ResponseEntity<UserLevelResponse> addExp(
            @CookieValue(name = "userToken", required = false) String userToken,
            @RequestBody AddExpRequest request
    ) {
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(userLevelService.addExp(accountId, request.getAmount()));
    }
}