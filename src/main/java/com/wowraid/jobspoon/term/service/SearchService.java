package com.wowraid.jobspoon.term.service;

import com.wowraid.jobspoon.term.service.request.SearchTermRequest;
import com.wowraid.jobspoon.term.service.response.SearchTermResponse;

public interface SearchService {
    SearchTermResponse search(SearchTermRequest request);
}
