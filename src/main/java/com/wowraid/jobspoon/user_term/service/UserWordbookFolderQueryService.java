package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.controller.response_form.MyFolderListResponseForm;

import java.util.List;
import java.util.Map;

public interface UserWordbookFolderQueryService {
    MyFolderListResponseForm getMyFolders(Long accountId);
    MyFolderListResponseForm getMyFoldersWithStats(Long accountId);

    /**
     * 특정 사용자의 폴더 목록 캐시를 무효화(삭제)하는 메서드
     * - 폴더를 추가, 수정, 삭제했을 때 기존 캐시가 유효하지 않으므로
     * - 이 메서드를 호출하여 캐시를 삭제하고 다음 조회 시 최신 데이터를 다시 캐싱하도록 합니다
     */
    void evictMyFoldersCache(Long accountId);
    void evictMyFoldersStatsCache(Long accountId);
    boolean existsByIdAndAccountId(Long folderId, Long accountId);

    // eBook 내보내기용 termId 수집(읽기 전용)
    UserWordbookFolderService.ExportTermIdsResult collectExportTermIds(Long accountId, Long folderId,
                                                                       String memorization,
                                                                       List<String> includeTags,
                                                                       List<String> excludeTags,
                                                                       String sort,
                                                                       int hardLimit);

    // 단어장 폴더에 있는 총 단어 개수를 즉시 확인하기
    long countTermsInFolderOrThrow(Long accountId, Long folderId);
}
