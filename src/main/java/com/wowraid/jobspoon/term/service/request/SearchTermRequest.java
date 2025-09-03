package com.wowraid.jobspoon.term.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchTermRequest {
    public enum SortKey { RELEVANCE, TITLE, UPDATED_AT }
    private final String q;
    private final int page;
    private final int size;
    private final SortKey sortKey;
    private final org.springframework.data.domain.Sort.Direction direction;
    private final boolean includeTags;
}
