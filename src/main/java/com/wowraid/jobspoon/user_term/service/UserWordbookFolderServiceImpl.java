package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.request.CreateUserWordbookFolderRequest;
import com.wowraid.jobspoon.user_term.service.request.ListUserWordbookTermRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateUserWordbookFolderResponse;
import com.wowraid.jobspoon.user_term.service.response.ListUserWordbookTermResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWordbookFolderServiceImpl implements UserWordbookFolderService {

    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserWordbookTermRepository userWordbookTermRepository;

    @Override
    @Transactional
    public CreateUserWordbookFolderResponse registerWordbookFolder(CreateUserWordbookFolderRequest request) {
        Long accountId = request.getAccountId();

        String raw = request.getFolderName();
        String normalized = normalize(raw).toLowerCase();
        if (normalized.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "폴더명을 입력해 주세요.");

        if (userWordbookFolderRepository.existsByAccount_IdAndNormalizedFolderName(accountId, normalized))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 폴더명입니다.");

        int nextOrder = userWordbookFolderRepository.findMaxSortOrderByAccountId(accountId) + 1;
        var entity = request.toUserWordbookFolder(nextOrder, normalized);

        try {
            var saved = userWordbookFolderRepository.save(entity);
            return CreateUserWordbookFolderResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // DB 유니크 제약(경합) → 409로 통일
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
