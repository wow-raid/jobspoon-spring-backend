package com.wowraid.jobspoon.profile_appearance.Controller;

import com.wowraid.jobspoon.profile_appearance.Controller.request_form.CustomNicknameRequest;
import com.wowraid.jobspoon.profile_appearance.Controller.request_form.PhotoRequest;
import com.wowraid.jobspoon.profile_appearance.Controller.response_form.AppearanceResponse;
import com.wowraid.jobspoon.profile_appearance.Service.ProfileAppearanceService;
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

    @GetMapping("/my")
    public ResponseEntity<AppearanceResponse> getMyAppearance(@RequestHeader("Authorization") String userToken){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AppearanceResponse response = appearanceService.getMyAppearance(accountId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/photo")
    public ResponseEntity<AppearanceResponse.PhotoResponse> updatePhoto(
            @RequestHeader("Authorization") String userToken,
            @RequestBody PhotoRequest request
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AppearanceResponse.PhotoResponse response = appearanceService.updatePhoto(accountId, request.getPhotoUrl());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/nickname")
    public ResponseEntity<AppearanceResponse.CustomNicknameResponse> updateNickname(
            @RequestHeader("Authorization") String userToken,
            @RequestBody CustomNicknameRequest request
            ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        AppearanceResponse.CustomNicknameResponse response =
                appearanceService.updateNickname(accountId, request.getCustomNickname());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/rank/{rankId}/equip")
    public ResponseEntity<AppearanceResponse.Rank> equipRank(
            @RequestHeader("Authorization") String userToken,
            @PathVariable Long rankId
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(appearanceService.equipRank(accountId, rankId));
    }

    @GetMapping("/rank/my")
    public ResponseEntity<List<AppearanceResponse.Rank>> getMyRanks(
            @RequestHeader("Authorization") String userToken
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(appearanceService.getMyRanks(accountId));
    }

    @PutMapping("title/{titleId}/equip")
    public ResponseEntity<AppearanceResponse.Title> equipTitle(
            @RequestHeader("Authorization") String userToken,
            @PathVariable Long titleId
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(appearanceService.equipTitle(accountId, titleId));
    }

    @GetMapping("/title/my")
    public ResponseEntity<List<AppearanceResponse.Title>> getMyTitles(
            @RequestHeader("Authorization") String userToken
    ){
        Long accountId = tokenAccountService.resolveAccountId(userToken);
        return ResponseEntity.ok(appearanceService.getMyTitles(accountId));
    }
}