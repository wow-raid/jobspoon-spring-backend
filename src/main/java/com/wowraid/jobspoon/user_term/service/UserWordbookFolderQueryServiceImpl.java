package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import com.wowraid.jobspoon.user_term.controller.response_form.FolderSummaryResponseForm;
import com.wowraid.jobspoon.user_term.controller.response_form.MyFolderListResponseForm;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.repository.projection.FolderCountRow;
import com.wowraid.jobspoon.user_term.repository.UserTermProgressRepository;
import com.wowraid.jobspoon.term.repository.TermTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWordbookFolderQueryServiceImpl implements UserWordbookFolderQueryService {

    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserWordbookTermRepository userWordbookTermRepository;
    private final UserTermProgressRepository userTermProgressRepository;
    private final TermTagRepository termTagRepository;
    private final RedisCacheService redisCacheService;

    @Value("${ebook.max.termids.per.folder:5000}")
    private int maxTermIdsPerFolder;

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
    public boolean existsByIdAndAccountId(Long folderId, Long accountId) {
        return userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
    }

    /**
     * eBook 내보내기용 termId 수집 (읽기 전용)
     * - 암기 상태/태그 포함/제외 필터 및 정렬, 상한 처리 포함
     */
    @Override
    public UserWordbookFolderService.ExportTermIdsResult collectExportTermIds(Long accountId, Long folderId,
                                                                              String memorization,
                                                                              List<String> includeTags,
                                                                              List<String> excludeTags,
                                                                              String sort,
                                                                              int hardLimit) {
        // 소유권 검증
        boolean owns = userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
        if (!owns) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다.");

        // 폴더 내 termId 전체 (중복 제거 + 정렬)
        List<Long> base = userWordbookTermRepository
                .findDistinctTermIdsByFolderAndAccountOrderByTermIdAsc(folderId, accountId);

        final int totalBefore = base.size();
        if (totalBefore == 0) {
            return new UserWordbookFolderService.ExportTermIdsResult(folderId, List.of(), 0, 0, false, hardLimit, 0);
        }

        // 필터 암기 상태
        List<Long> filtered = new ArrayList<>(base);
        if (memorization != null && !memorization.isBlank()) {
            var rows = userTermProgressRepository.findByIdAccountIdAndIdTermIdIn(accountId, filtered);
            Map<Long, String> statusMap = new HashMap<>();
            for (Long id : filtered) statusMap.put(id, "LEARNING"); // 기본값
            rows.forEach(p -> statusMap.put(p.getId().getTermId(), p.getStatus().name()));

            final String want = memorization.toUpperCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(id -> Objects.equals(statusMap.get(id), want))
                    .collect(Collectors.toList());
        }

        // 태그 포함/제외 (include 먼저, exclude 다음)
        if (includeTags != null && !includeTags.isEmpty()) {
            var rows = termTagRepository.findTermIdAndTagNameByTermIdIn(filtered);
            Map<Long, Set<String>> byTerm = new HashMap<>();
            rows.forEach(r -> byTerm.computeIfAbsent(r.getTermId(), k -> new HashSet<>()).add(r.getTagName()));
            filtered = filtered.stream()
                    .filter(id -> {
                        Set<String> have = byTerm.getOrDefault(id, Set.of());
                        for (String tag : includeTags) {
                            if (!have.contains(tag)) return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        if (excludeTags != null && !excludeTags.isEmpty()) {
            var rows = termTagRepository.findTermIdAndTagNameByTermIdIn(filtered);
            Map<Long, Set<String>> byTerm = new HashMap<>();
            rows.forEach(r -> byTerm.computeIfAbsent(r.getTermId(), k -> new HashSet<>()).add(r.getTagName()));
            filtered = filtered.stream()
                    .filter(id -> {
                        Set<String> have = byTerm.getOrDefault(id, Set.of());
                        for (String tag : excludeTags) {
                            if (have.contains(tag)) return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // 정렬 : termId ASC/DESC
        if (sort != null && !sort.isBlank()) {
            String[] p = sort.split(",", 2);
            boolean desc = p.length > 1 && "DESC".equalsIgnoreCase(p[1]);
            filtered.sort(desc ? Comparator.<Long>naturalOrder().reversed() : Comparator.naturalOrder());
        }

        int filteredOut = totalBefore - filtered.size();

        // 상한
        int limit = hardLimit > 0 ? hardLimit : maxTermIdsPerFolder;
        boolean exceeded = filtered.size() >= limit;
        List<Long> finalIds = exceeded ? List.of() : filtered;

        return new UserWordbookFolderService.ExportTermIdsResult(
                folderId,
                finalIds,
                totalBefore,
                filteredOut,
                exceeded,
                limit,
                finalIds.size()
        );
    }
}
