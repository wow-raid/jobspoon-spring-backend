package com.wowraid.jobspoon.user_term.service.response;

import com.wowraid.jobspoon.user_term.entity.FavoriteTerm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateFavoriteTermResponse {
    private final String message;
    private final Long favoriteTermId;
    private final String favoriteTermName;

    public static CreateFavoriteTermResponse from(FavoriteTerm favoriteTerm) {
        String message = "해당 용어를 즐겨찾기 등록하는데 성공했습니다.";
        return new CreateFavoriteTermResponse(message, favoriteTerm.getId(), favoriteTerm.getTerm().getTitle());
    }
}
