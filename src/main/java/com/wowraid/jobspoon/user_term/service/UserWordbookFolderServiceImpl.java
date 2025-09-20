package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.user_term.entity.UserWordbookFolder;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.request.*;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookTermResponse;
import com.wowraid.jobspoon.user_term.service.response.ListUserWordbookTermResponse;
import com.wowraid.jobspoon.user_term.service.response.MoveFolderTermsResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWordbookFolderServiceImpl implements UserWordbookFolderService {

    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserWordbookTermRepository userWordbookTermRepository;
    private final AccountRepository accountRepository;
    private final TermRepository termRepository;

    @Override
    @Transactional
    public CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request) {
        Long accountId = request.getAccountId();

        String raw = request.getFolderName();
        String normalized = normalize(raw).toLowerCase();
        if (normalized.isBlank())
            throw new ResponseStatusException(BAD_REQUEST, "폴더명을 입력해 주세요.");

        if (userWordbookFolderRepository.existsByAccount_IdAndNormalizedFolderName(accountId, normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 폴더명입니다.");

        int nextOrder = userWordbookFolderRepository.findMaxSortOrderByAccountId(accountId) + 1;
        var entity = request.toUserWordbookFolder(nextOrder, normalized);

        try {
            var saved = userWordbookFolderRepository.save(entity);
            return CreateUserWordbookFolderResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 폴더명입니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ListUserWordbookTermResponse list(ListUserWordbookTermRequest request) {
        Long accountId = request.getAccountId();
        Long folderId = request.getFolderId();

        boolean owns = userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
        log.info("[list] owns? accountId={}, folderId={}, result={}", accountId, folderId, owns);
        if (!owns) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 폴더를 찾을 수 없습니다.");

        int pageIdx = Math.max(0, request.getPage() - 1);
        int size = Math.max(1, request.getPerPage());
        Sort sort = parseSortOrDefault(request.getSort(), Sort.by(Sort.Order.desc("createdAt")));
        Pageable pageable = PageRequest.of(pageIdx, size, sort);

        log.info("[list] call repo: folderId={}, accountId={}, pageable={}", folderId, accountId, pageable);

        Page<UserWordbookTerm> paginatedList =
                userWordbookTermRepository.findPageByFolderAndOwnerFetch(folderId, accountId, pageable);

        return ListUserWordbookTermResponse.from(paginatedList);
    }

    /** 폴더 전체 재정렬 */
    @Override
    @Transactional
    public void reorder(ReorderUserWordbookFoldersRequest request) {
        final Long accountId = request.getAccountId();
        final List<Long> orderedIds = Optional.ofNullable(request.getOrderedIds())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "ids가 필요합니다."));

        final Set<Long> dedup = new LinkedHashSet<>(orderedIds);
        if (dedup.size() != orderedIds.size())
            throw new ResponseStatusException(BAD_REQUEST, "ids에 중복이 포함되어 있습니다.");

        final List<UserWordbookFolder> all =
                userWordbookFolderRepository.findAllByAccount_IdOrderBySortOrderAscIdAsc(accountId);
        if (all.isEmpty() && !orderedIds.isEmpty())
            throw new ResponseStatusException(BAD_REQUEST, "정렬할 폴더가 존재하지 않습니다.");

        final Set<Long> allIds = all.stream().map(UserWordbookFolder::getId).collect(Collectors.toSet());
        if (!allIds.equals(dedup))
            throw new ResponseStatusException(BAD_REQUEST, "ids가 계정의 폴더 전체 집합과 일치하지 않습니다.");

        int idx = 0;
        Map<Long, Integer> toOrder = new HashMap<>();
        for (Long id : orderedIds) toOrder.put(id, idx++);

        for (UserWordbookFolder folder : all) {
            Integer newOrder = toOrder.get(folder.getId());
            if (newOrder == null) throw new ResponseStatusException(NOT_FOUND, "요청하신 폴더를 찾을 수 없습니다.");
            if (!Objects.equals(folder.getSortOrder(), newOrder)) {
                folder.setSortOrder(newOrder);
            }
        }
        userWordbookFolderRepository.saveAll(all);
    }

    @Override
    @Transactional
    public CreateUserWordbookTermResponse attachTerm(CreateUserWordbookTermRequest request) {
        Long accountId = request.getAccountId();
        Long folderId  = request.getFolderId();
        Long termId    = request.getTermId();

        if (accountId == null || folderId == null || termId == null || accountId <= 0 || folderId <= 0 || termId <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "잘못된 파라미터입니다.");
        }

        boolean owns = userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
        if (!owns) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "폴더에 대한 권한이 없습니다.");

        Optional<UserWordbookTerm> existing = userWordbookTermRepository
                .findByAccount_IdAndFolder_IdAndTerm_Id(accountId, folderId, termId);
        if (existing.isPresent()) {
            return CreateUserWordbookTermResponse.alreadyAttached(existing.get().getId(), folderId, termId);
        }

        if (!termRepository.existsById(termId)) {
            throw new ResponseStatusException(NOT_FOUND, "용어를 찾을 수 없습니다.");
        }

        UserWordbookFolder folderRef = userWordbookFolderRepository.getReferenceById(folderId);
        Account accountRef           = accountRepository.getReferenceById(accountId);
        Term termRef                 = termRepository.getReferenceById(termId);

        UserWordbookTerm uwt = new UserWordbookTerm(accountRef, folderRef, termRef);
        UserWordbookTerm saved = userWordbookTermRepository.save(uwt);
        return CreateUserWordbookTermResponse.created(saved.getId(), folderId, termId);
    }

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(value = "folderTerms", allEntries = true) })
    public MoveFolderTermsResponse moveTerms(
            Long accountId, Long sourceFolderId, Long targetFolderId, List<Long> termIds) {

        if (Objects.equals(sourceFolderId, targetFolderId)) {
            log.info("[moveTerms] SAME_FOLDER accountId={} folderId={} requestedIds={}",
                    accountId, sourceFolderId, termIds);
            final List<Long> ids = (termIds == null) ? Collections.emptyList() : termIds;
            return MoveFolderTermsResponse.sameFolder(sourceFolderId, ids);
        }
        if (termIds == null || termIds.isEmpty()) {
            return new MoveFolderTermsResponse(sourceFolderId, targetFolderId, 0, List.of(), List.of());
        }

        // 폴더 소유 검증
        var source = userWordbookFolderRepository.findById(sourceFolderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "소스 폴더를 찾을 수 없습니다."));
        var target = userWordbookFolderRepository.findById(targetFolderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 폴더를 찾을 수 없습니다."));

        if (!Objects.equals(source.getAccount().getId(), accountId) ||
                !Objects.equals(target.getAccount().getId(), accountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        // 입력 정규화
        List<Long> distinctTermIds = termIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 결과 집계 변수들 (★ 누락되기 쉬운 부분)
        int moved = 0;
        List<Long> movedTermIds = new ArrayList<>();
        List<MoveFolderTermsResponse.Skipped> skipped = new ArrayList<>();

        // 대상 폴더의 현재 최대 sortOrder (account 조건 제거 버전)
        int baseOrder = Optional.ofNullable(
                userWordbookTermRepository.findMaxSortOrderByFolder(targetFolderId)
        ).orElse(0);
        int cursor = baseOrder;

        for (Long termId : distinctTermIds) {
            var srcOpt = userWordbookTermRepository.findByFolder_IdAndTerm_Id(sourceFolderId, termId);
            if (srcOpt.isEmpty()) {
                skipped.add(new MoveFolderTermsResponse.Skipped(
                        termId, MoveFolderTermsResponse.Skipped.Reason.NOT_IN_SOURCE));
                continue;
            }

            boolean inTarget = userWordbookTermRepository.existsByFolder_IdAndTerm_Id(targetFolderId, termId);
            if (inTarget) {
                skipped.add(new MoveFolderTermsResponse.Skipped(
                        termId, MoveFolderTermsResponse.Skipped.Reason.DUPLICATE_IN_TARGET));
                continue;
            }

            Term termRef = termRepository.getReferenceById(termId);
            var created = UserWordbookTerm.of(target, termRef, ++cursor); // account는 folder에서 자동 세팅
            userWordbookTermRepository.save(created);

            userWordbookTermRepository.deleteById(srcOpt.get().getId());
            moved++;
            movedTermIds.add(termId);
        }

        int dupe = (int) skipped.stream()
                .filter(s -> s.getReason() == MoveFolderTermsResponse.Skipped.Reason.DUPLICATE_IN_TARGET)
                .count();
        int notInSrc = (int) skipped.stream()
                .filter(s -> s.getReason() == MoveFolderTermsResponse.Skipped.Reason.NOT_IN_SOURCE)
                .count();
        int notFound = (int) skipped.stream()
                .filter(s -> s.getReason() == MoveFolderTermsResponse.Skipped.Reason.TERM_NOT_FOUND)
                .count();

        log.info("[moveTerms] accountId={} {} -> {} | requested={} moved={} skipped={} (dupe={}, notInSource={}, notFound={}) | movedTermIds={}",
                accountId, sourceFolderId, targetFolderId,
                distinctTermIds.size(), moved, skipped.size(), dupe, notInSrc, notFound, movedTermIds);

        return new MoveFolderTermsResponse(sourceFolderId, targetFolderId, moved, skipped, movedTermIds);
    }

    /* ------------------------------------- */

    private Sort parseSortOrDefault(String sortParam, Sort defaultSort) {
        if (sortParam == null || sortParam.isBlank()) return defaultSort;

        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "desc";

        // UserWordbookTerm 기준 화이트리스트
        switch (property) {
            case "createdAt":
            case "lastReviewedAt": break;
            default: property = "createdAt";
        }
        return "asc".equals(direction) ? Sort.by(property).ascending() : Sort.by(property).descending();
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }
}
