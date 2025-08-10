package com.wowraid.jobspoon.user_term.service;

import com.wowraid.jobspoon.user_term.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.user_term.service.response.CreateFavoriteTermResponse;
import org.springframework.http.ResponseEntity;

public interface FavoriteTermService {
    CreateFavoriteTermResponse registerFavoriteTerm(CreateFavoriteTermRequest request);
    ResponseEntity<?> deleteFavoriteTerm(Long favoriteTermId);
}
