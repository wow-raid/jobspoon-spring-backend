package com.wowraid.jobspoon.user_term.service.request;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateFavoriteTermRequest {
    private final Long accountId;
    private final Long termId;

    public FavoriteTerm toFavoriteTerm(Term term) {
        return new FavoriteTerm(term);
    }
}