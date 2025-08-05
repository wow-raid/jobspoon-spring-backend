package com.wowraid.jobspoon.kakao_oauth.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.account_profile.service.AccountProfileService;
import com.wowraid.jobspoon.account_profile.dto.AccountProfileRequest;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoTokenResponse;
import com.wowraid.jobspoon.kakao_oauth.dto.KakaoUserInfoResponse;
import com.wowraid.jobspoon.kakao_oauth.repository.KakaoOauthRepositoryImpl;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoOauthServiceImpl implements KakaoOauthService {

    private final KakaoOauthRepositoryImpl kakaoOauthRepository;
    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final RedisCacheService redisCacheService;

    // 카카오 로그인 링크 요청
    @Override
    public String requestKakaoOauthLink() {
        return kakaoOauthRepository.getOauthLink();
    }

    // 인가 코드를 바탕으로 access token 요청
    @Override
    public KakaoTokenResponse requestAccessToken(String code) {
        return kakaoOauthRepository.getAccessToken(code);
    }

    // access token을 바탕으로 사용자 정보 요청
    @Override
    public KakaoUserInfoResponse requestUserInfo(String accessToken) {
        return kakaoOauthRepository.getUserInfo(accessToken);
    }

    // 카카오 계정 연결 해제 요청
    @Override
    public String requestKakaoWithdrawLink(String accessToken) {
        return kakaoOauthRepository.getWithdrawLink(accessToken);
    }

    // 카카오 로그인 또는 회원가입 수행
    @Override
    public String loginOrSignUpViaKakao(String code) {
        // 1. 인가 코드를 바탕으로 access token 요청
        KakaoTokenResponse tokenResponse = kakaoOauthRepository.getAccessToken(code);
        String accessToken = tokenResponse.getAccessToken();

        // 2. access token으로 사용자 정보 요청
        KakaoUserInfoResponse userInfo = kakaoOauthRepository.getUserInfo(accessToken);
        KakaoUserInfoResponse.KakaoAccount kakaoAccount = userInfo.getKakaoAccount();
        KakaoUserInfoResponse.KakaoProperties properties = userInfo.getProperties();

        // 3. 사용자 정보에서 필요한 필드 추출
        String email = kakaoAccount.getEmail();
        String nickname = properties.getNickname();
        String gender = kakaoAccount.getGender();
        String ageRange = kakaoAccount.getAgeRange();
        String birthyear = kakaoAccount.getBirthyear();
        String loginType = "KAKAO";

        Account account;
        boolean isDuplicated = accountService.checkEmailDuplication(email);

        // 4. 계정이 없으면 생성하고, 있으면 기존 계정 조회
        if (!isDuplicated) {
            // 계정 생성
            accountService.createAccount(email, loginType);
            account = accountService.getAccountByEmail(email);

            // 프로필 요청 객체 생성
            AccountProfileRequest profileRequest = new AccountProfileRequest();
            profileRequest.setNickname(nickname);
            profileRequest.setGender(gender);
            profileRequest.setBirthyear(birthyear);
            profileRequest.setAgeRange(ageRange);

            // 프로필 생성
            accountProfileService.createAccountProfile(account.getId(), profileRequest);
        } else {
            account = accountService.getAccountByEmail(email);
        }

        // 5. Redis에 access token과 계정 ID 저장 후 userToken 반환
        return generateRedisUserToken(account.getId(), accessToken);
    }

    // Redis에 access token/accountId 저장하고, 사용자 식별용 userToken 생성
    private String generateRedisUserToken(Long accountId, String accessToken) {
        String userToken = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(accountId.toString(), accessToken);
        redisCacheService.setKeyAndValue(userToken, accountId.toString());
        return userToken;
    }
}
