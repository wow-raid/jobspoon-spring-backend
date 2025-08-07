package com.wowraid.jobspoon.wordbook.service.request;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.wordbook.entity.FavoriteTerm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateFavoriteTermRequest {
    private final Long accountId;
    private final Long termId;

    public FavoriteTerm toFavoriteTerm(Account account, Term term) {
        return new FavoriteTerm(account, term);
    }
}
