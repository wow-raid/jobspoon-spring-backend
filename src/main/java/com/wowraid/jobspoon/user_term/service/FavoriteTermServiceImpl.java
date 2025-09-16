package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import com.wowraid.jobspoon.user_term.repository.FavoriteTermRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookFolderRepository;
import com.wowraid.jobspoon.user_term.repository.UserWordbookTermRepository;
import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.user_term.service.request.MoveFavoritesRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateFavoriteTermResponse;
import com.wowraid.jobspoon.user_term.service.response.MoveFavoritesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteTermServiceImpl implements FavoriteTermService {

    private final TermRepository termRepository;
    private final AccountRepository accountRepository;
    private final FavoriteTermRepository favoriteTermRepository;
    private final UserWordbookFolderRepository userWordbookFolderRepository;
    private final UserWordbookTermRepository userWordbookTermRepository;

    @Override
    public CreateFavoriteTermResponse registerFavoriteTerm(CreateFavoriteTermRequest request) {

        // 존재하지 않는 용어 ID 입력 시 에러 처리
        Term maybeTerm = termRepository.findById(request.getTermId())
                .orElseThrow(()-> new IllegalArgumentException("즐겨찾기 하고자 하는 용어가 없습니다."));

        // 존재하지 않는 사용자 ID 입력 시 에러 처리
        Account maybeAccount = accountRepository.findById(request.getAccountId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이미 즐겨찾기로 등록된 경우 예외 처리
        if (favoriteTermRepository.existsByAccountAndTerm(maybeAccount, maybeTerm)) {
            throw new IllegalArgumentException("이미 즐겨찾기로 등록된 용어입니다.");
        }

        // 즐겨찾기하고자 하는 객체 생성 및 저장
        FavoriteTerm favoriteTerm = request.toFavoriteTerm(maybeTerm);
        FavoriteTerm savedFavoriteTerm = favoriteTermRepository.save(favoriteTerm);

        return CreateFavoriteTermResponse.from(savedFavoriteTerm);
    }

    @Override
    public ResponseEntity<?> deleteFavoriteTerm(Long favoriteTermId) {
        FavoriteTerm favoriteTerm = favoriteTermRepository.findById(favoriteTermId)
                .orElseThrow(()-> new IllegalArgumentException("해당 즐겨찾기 항목이 존재하지 않습니다."));
        favoriteTermRepository.delete(favoriteTerm);
        return ResponseEntity.ok().body("즐겨찾기 용어를 성공적으로 삭제했습니다.");
    }

    @Override
    @Transactional
    public MoveFavoritesResponse moveToFolder(MoveFavoritesRequest request) {
        Long accountId = request.getAccountId();
        Long targetFolderId = request.getTargetFolderId();

        // 1) 대상 폴더 존재/소유 검증
        var folder = userWordbookFolderRepository.findById(targetFolderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."));
        if (!folder.getAccount().getId().equals(accountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        // 2) 이동할 termId 목록 구성
        List<Long> termIds = new ArrayList<>();
        if (request.getTermIds() != null && !request.getTermIds().isEmpty()) {
            // 클라이언트가 termIds를 직접 보낸 경우
            termIds.addAll(request.getTermIds());
        } else if (request.getFavoriteIds() != null && !request.getFavoriteIds().isEmpty()) {
            // favoriteIds로 넘어온 경우 termId로 해석
            var favs = favoriteTermRepository.findAllById(request.getFavoriteIds());
            for (var f : favs) {
                if (Objects.equals(f.getAccount().getId(), accountId)) {
                    termIds.add(f.getTerm().getId());
                }
            }
        }
        termIds = termIds.stream().filter(Objects::nonNull).distinct().toList();
        if (termIds.isEmpty()) {
            return MoveFavoritesResponse.empty(targetFolderId);
        }

        // 3) 대상 폴더의 마지막 sortOrder 다음부터 부여
        Integer baseOrderObj = userWordbookTermRepository.findMaxSortOrderByAccountAndFolder(accountId, targetFolderId);
        int cursor = (baseOrderObj != null ? baseOrderObj : 0);

        int moved = 0;
        List<MoveFavoritesResponse.Skipped> skipped = new ArrayList<>();

        for (Long termId : termIds) {
            // 3-1) 이미 대상 폴더에 있으면 스킵
            boolean exists = userWordbookTermRepository
                    .existsByAccount_IdAndFolder_IdAndTerm_Id(accountId, targetFolderId, termId);
            if (exists) {
                skipped.add(new MoveFavoritesResponse.Skipped(
                        termId, MoveFavoritesResponse.Skipped.Reason.DUPLICATE_IN_TARGET));
                continue;
            }

            // 3-2) 즐겨찾기 row(있으면 삭제용으로 잡아두기)
            var favOpt = favoriteTermRepository.findByAccount_IdAndTerm_Id(accountId, termId);

            // 3-3) 엔티티 생성(세터 없이)
            var termRef = termRepository.getReferenceById(termId);
            var entity = UserWordbookTerm.of(folder, termRef, ++cursor);
            userWordbookTermRepository.save(entity);

            // 3-4) 즐겨찾기 삭제(있을 때만)
            favOpt.ifPresent(favoriteTermRepository::delete);
            moved++;
        }

        return new MoveFavoritesResponse(targetFolderId, moved, skipped);
    }

}
