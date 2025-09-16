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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 폴더 전체 재정렬
     * 정책 : orderedIds 는 해당 계정의 폴더 전체를 id를 "최종 순서"로 모두 포함해야 함
     */

    @Override
    @Transactional
    public void reorder(ReorderUserWordbookFoldersRequest request) {
        final Long accountId = request.getAccountId();
        final List<Long> orderedIds = Optional.ofNullable(request.getOrderedIds())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "ids가 필요합니다."));

        final Set<Long> dedup = new LinkedHashSet<>(orderedIds);
        if (dedup.size() != orderedIds.size())
            throw new ResponseStatusException(BAD_REQUEST, "ids에 중복이 포함되어 있습니다.");

        // 계정 전체 폴더 조회 (관리 엔티티로)
        final List<UserWordbookFolder> all =
                userWordbookFolderRepository.findAllByAccount_IdOrderBySortOrderAscIdAsc(accountId);
        if (all.isEmpty() && !orderedIds.isEmpty())
            throw new ResponseStatusException(BAD_REQUEST, "정렬할 폴더가 존재하지 않습니다.");

        final Set<Long> allIds = all.stream().map(UserWordbookFolder::getId).collect(Collectors.toSet());
        if (!allIds.equals(dedup))
            throw new ResponseStatusException(BAD_REQUEST, "ids가 계정의 폴더 전체 집합과 일치하지 않습니다.");

        // id -> 새 sortOrder
        int idx = 0;
        Map<Long, Integer> toOrder = new HashMap<>();
        for (Long id : orderedIds) toOrder.put(id, idx++);

        for (UserWordbookFolder folder : all) {
            Integer newOrder = toOrder.get(folder.getId());
            if (newOrder == null) throw new ResponseStatusException(NOT_FOUND, "요청하신 폴더를 찾을 수 없습니다.");
            // 핵심: id와 비교 X, 현재 sortOrder와 비교
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

        // 입력값 유효성 (null/음수 방어)
        if (accountId == null || folderId == null || termId == null || accountId <= 0 || folderId <= 0 || termId <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "잘못된 파라미터입니다.");
        }

        // 소유권 검증
        boolean owns = userWordbookFolderRepository.existsByIdAndAccount_Id(folderId, accountId);
        if (!owns) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "폴더에 대한 권한이 없습니다.");

        // 멱등 체크
        Optional<UserWordbookTerm> existing = userWordbookTermRepository
                .findByAccount_IdAndFolder_IdAndTerm_Id(accountId, folderId, termId);

        if (existing.isPresent()) {
            return CreateUserWordbookTermResponse.alreadyAttached(existing.get().getId(), folderId, termId);
        }

        // 존재성 검증(termId)
        if (!termRepository.existsById(termId)) {
            throw new ResponseStatusException(NOT_FOUND, "용어를 찾을 수 없습니다.");
        }

        // 연관 엔티티 참조 로딩 (프록시 OK)
        UserWordbookFolder folderRef = userWordbookFolderRepository.getReferenceById(folderId);
        Account accountRef           = accountRepository.getReferenceById(accountId);
        Term termRef                 = termRepository.getReferenceById(termId);

        // 저장
        UserWordbookTerm uwt = new UserWordbookTerm(accountRef, folderRef, termRef);

        UserWordbookTerm saved = userWordbookTermRepository.save(uwt);
        return CreateUserWordbookTermResponse.created(saved.getId(), folderId, termId);
    }

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
