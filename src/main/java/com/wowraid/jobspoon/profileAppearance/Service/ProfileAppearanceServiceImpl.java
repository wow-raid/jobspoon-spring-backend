package com.wowraid.jobspoon.profileAppearance.Service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.profileAppearance.Controller.response.AppearanceResponse;
import com.wowraid.jobspoon.profileAppearance.Entity.ProfileAppearance;
import com.wowraid.jobspoon.profileAppearance.Repository.ProfileAppearanceRepository;
import com.wowraid.jobspoon.userLevel.repository.UserLevelHistoryRepository;
import com.wowraid.jobspoon.userTitle.repository.UserTitleRepository;
import com.wowraid.jobspoon.userTrustscore.repository.TrustScoreRepository;
import com.wowraid.jobspoon.userLevel.repository.UserLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileAppearanceServiceImpl implements ProfileAppearanceService {

    private final ProfileAppearanceRepository appearanceRepository;
    private final UserTitleRepository titleRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final TrustScoreRepository trustScoreRepository;
    private final UserLevelRepository userLevelRepository;
    private final S3Service s3Service;
    private final UserLevelHistoryRepository userLevelHistoryRepository;

    /** 회원 가입 시 호출 **/
    @Override
    public Optional<ProfileAppearance> create(Long accountId) {
        ProfileAppearance pa = ProfileAppearance.init(accountId);
        return Optional.of(appearanceRepository.save(pa));
    }

    /** 회원 탈퇴 시 호출 **/
    @Override
    public void delete(Long accountId) {
        // [수정] 존재하지 않을 경우 예외 던지도록 수정
        if (!appearanceRepository.existsByAccountId(accountId)) {
            throw new IllegalArgumentException("ProfileAppearance not found for accountId=" + accountId);
        }

        // 1. 칭호 이력 삭제
        titleRepository.deleteAllByAccountId(accountId);

        // 2. 신뢰점수 삭제
        trustScoreRepository.deleteAllByAccountId(accountId);

        // 3. 레벨 삭제
        userLevelRepository.deleteByAccountId(accountId);
        userLevelHistoryRepository.deleteByAccountId(accountId);

        // 4. 프로필 외형 삭제
        appearanceRepository.deleteByAccountId(accountId);
    }

    /** 프로필 조회 **/
    @Override
    @Transactional(readOnly = true)
    public AppearanceResponse getMyAppearance(Long accountId) {

        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseGet(() -> appearanceRepository.save(ProfileAppearance.init(accountId)));

        AccountProfile ap = accountProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("AccountProfile not found"));

        // Presigned URL 생성 (없으면 null)
        String presignedUrl = (pa.getPhotoKey() != null)
                ? s3Service.generateDownloadUrl(pa.getPhotoKey())
                : null;

        return AppearanceResponse.of(pa, ap, presignedUrl);
    }

    /** 사진 업데이트 (photoKey 저장) **/
    @Override
    public AppearanceResponse.PhotoResponse updatePhoto(Long accountId, String photoKey) {
        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));

        pa.setPhotoKey(photoKey);
        appearanceRepository.save(pa);

        return new AppearanceResponse.PhotoResponse(pa.getPhotoKey());
    }

    // Presigned Upload URL 발급 + 기존 파일 삭제 + DB 저장
    public String generateUploadUrl(Long accountId, String filename, String contentType) {
        // 기존 파일 삭제
        String oldKey = null;

        try {
            oldKey = getPhotoKey(accountId);
            if( oldKey != null && !oldKey.isBlank() ) {
                s3Service.deleteFile(oldKey);
            }
        } catch (Exception e) {
            System.err.println("⚠️ 기존 프로필 삭제 실패: " + e.getMessage());
        }

        // 확장자 추출 (없으면 기본 .png)
        String extension = "";
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = filename.substring(dotIndex); // ".png"
        } else {
            extension = ".png";
        }

        // 새로운 key 생성
        String key = String.format("profile/%d/%s%s", accountId, UUID.randomUUID(), extension);

        // Presigned URL 발급
        String presignedUrl = s3Service.generateUploadUrl(key, contentType);

        // DB에 새 key 저장
        updatePhoto(accountId, key);

        return presignedUrl;
    }

    /** 사진 key 조회 **/
    @Override
    @Transactional(readOnly = true)
    public String getPhotoKey(Long accountId) {
        ProfileAppearance pa = appearanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("ProfileAppearance not found"));
        return pa.getPhotoKey();
    }

    // Presigned Download URL 발급
    public String generateDownloadUrl(Long accountId) {
        String key = getPhotoKey(accountId);

        return s3Service.generateDownloadUrl(key);
    }
}
