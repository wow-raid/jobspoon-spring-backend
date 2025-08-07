package com.wowraid.jobspoon.wordbook.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.wordbook.entity.FavoriteTerm;
import com.wowraid.jobspoon.wordbook.repository.FavoriteTermRepository;
import com.wowraid.jobspoon.wordbook.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.wordbook.service.response.CreateFavoriteTermResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordBookServiceImpl implements WordBookService {

    private final TermRepository termRepository;
    private final AccountRepository accountRepository;
    private final FavoriteTermRepository favoriteTermRepository;

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
        FavoriteTerm favoriteTerm = request.toFavoriteTerm(maybeAccount, maybeTerm);
        FavoriteTerm savedFavoriteTerm = favoriteTermRepository.save(favoriteTerm);

        return CreateFavoriteTermResponse.from(savedFavoriteTerm);
    }
}
