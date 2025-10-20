package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.user_term.entity.enums.MemorizationStatus;
import com.wowraid.jobspoon.user_term.entity.UserTermProgress;
import com.wowraid.jobspoon.user_term.repository.UserTermProgressRepository;
import com.wowraid.jobspoon.user_term.service.request.UpdateMemorizationRequest;
import com.wowraid.jobspoon.user_term.service.response.UpdateMemorizationResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemorizationServiceImpl implements MemorizationService {

    private final UserTermProgressRepository userTermProgressRepository;
    private final TermRepository termRepository;
    private final EntityManager em;
    private final UserWordbookFolderQueryService userWordbookFolderQueryService;

    @Transactional
    @Override
    public UpdateMemorizationResponse updateMemorization(UpdateMemorizationRequest request) {

        if(request.getAccountId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-Account-Id 헤더가 필요합니다.");
        }

        // Term 존재 / 유효성 검사
        Term term = termRepository.findById(request.getTermId())
                .orElseThrow(() -> new IllegalArgumentException("용어가 존재하지 않습니다."));

        // 진행 정보 조회 or 생성
        var id = new UserTermProgress.Id(request.getAccountId(), request.getTermId());
        var termRef = em.getReference(Term.class, request.getTermId());

        var progress = userTermProgressRepository.findById(id)
                .orElseGet(() -> UserTermProgress.newOf(request.getAccountId(), termRef));


        log.info("[svc] progress before change: status={}, memorizedAt={}",
                progress.getStatus(), progress.getMemorizedAt());

        // 동일 상태면 변화 없음 응답
        MemorizationStatus before = progress.getStatus();
        if(before == request.getStatus()) {
            return UpdateMemorizationResponse.builder()
                        .termId(request.getTermId())
                        .status(before)
                        .memorizedAt(progress.getMemorizedAt())
                        .changed(false)
                        .build();
        }

        log.info("[svc] before: status={}, memorizedAt={}", progress.getStatus(), progress.getMemorizedAt());
        log.info("[svc] request: {}", request.getStatus());
        // 상태 전환 적용(→ MEMORIZED 시 memorizedAt 기록, → LEARNING 시 null)
        progress.changeStatus(request.getStatus());
        log.info("[svc] after: status={}, memorizedAt={}", progress.getStatus(), progress.getMemorizedAt());

        // 저장 및 응답
        UserTermProgress savedProgress = userTermProgressRepository.save(progress);

        // 커밋 후 캐시 무효화 (폴더 통계 캐시: LearnedCount 등 반영)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                userWordbookFolderQueryService.evictMyFoldersStatsCache(request.getAccountId());
            }
        });

        return UpdateMemorizationResponse.builder()
                .termId(request.getTermId())
                .status(before)
                .memorizedAt(progress.getMemorizedAt())
                .changed(true)
                .build();
    }
}
