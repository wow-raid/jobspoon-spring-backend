package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.user_term.controller.response_form.FolderSummaryResponseForm;
import com.wowraid.jobspoon.user_term.controller.response_form.MyFolderListResponseForm;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.projection.FolderCountRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWordbookFolderQueryServiceImpl implements UserWordbookFolderQueryService {

    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final RedisCacheService redisCacheService;

    private String key(Long accountId) {
        return "folders:" + accountId;
    }

    private static final Duration TTL = Duration.ofMinutes(10);

    @Override
    public MyFolderListResponseForm getMyFolders(Long accountId) {
        // 1) 캐시 히트 시 반환
        var cached = redisCacheService.getValueByKey(key(accountId), MyFolderListResponseForm.class);
        if (cached != null) {
            return cached;
        }

        // 2) DB 조회
        List<FolderCountRow> rows = userWordbookFolderRepository.findMyFoldersWithFavoriteCount(accountId);
        var response = MyFolderListResponseForm.builder()
                .folders(rows.stream()
                        .map(r -> FolderSummaryResponseForm.builder()
                                .id(r.id()).name(r.name()).termCount(r.termCount())
                                .build())
                        .toList())
                .build();

        // 3) 캐싱
        redisCacheService.setKeyAndValue(key(accountId), response, TTL);
        return response;
    }

    @Override
    public void evictMyFoldersCache(Long accountId) {
        redisCacheService.deleteByKey(key(accountId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndAccountId(Long folderId, Long accountId) {
        return userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
    }
}
