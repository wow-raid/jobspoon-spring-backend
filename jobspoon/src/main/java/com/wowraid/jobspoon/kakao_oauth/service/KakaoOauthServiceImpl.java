package com.wowraid.jobspoon.kakao_oauth.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.account_profile.service.AccountProfileService;
import com.wowraid.jobspoon.account_profile.service.request_form.AccountProfileRequest;
import com.wowraid.jobspoon.kakao_oauth.repository.KakaoOauthRepositoryImpl;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
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
    public Map<String, Object> requestAccessToken(String code) {
        return kakaoOauthRepository.getAccessToken(code);
    }

    // access token을 바탕으로 사용자 정보 요청
    @Override
    public Map<String, Object> requestUserInfo(String accessToken) {
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
        Map<String, Object> tokenResponse = requestAccessToken(code);
        String accessToken = (String) tokenResponse.get("access_token");

        // 2. access token으로 사용자 정보 요청
        Map<String, Object> userInfo = requestUserInfo(accessToken);
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");

        // 3. 사용자 정보에서 필요한 필드 추출
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) properties.get("nickname");
        String gender = (String) kakaoAccount.get("gender");
        String ageRange = (String) kakaoAccount.get("age_range");
        String birthyear = (String) kakaoAccount.get("birthyear");
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
