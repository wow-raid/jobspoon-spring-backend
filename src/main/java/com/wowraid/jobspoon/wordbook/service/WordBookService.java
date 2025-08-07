package com.wowraid.jobspoon.wordbook.service;

import com.wowraid.jobspoon.wordbook.service.request.CreateFavoriteTermRequest;
import com.wowraid.jobspoon.wordbook.service.response.CreateFavoriteTermResponse;

public interface WordBookService {
    CreateFavoriteTermResponse registerFavoriteTerm(CreateFavoriteTermRequest request);
}
