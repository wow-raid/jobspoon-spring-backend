package com.wowraid.jobspoon.term.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchTermRequest {
    public enum SortKey { RELEVANCE, TITLE, UPDATED_AT }

    // 검색어 (없을 수 있음 : prefix 모드일 땐 무시 가능)
    private final String q;

    // 상호 배타 prefix 파라미터 (하나만 세팅됨)
    private final String initial;   // ㄱ~ㅎ (더블초성은 폼에서 ㄱ/ㄷ/ㅂ/ㅅ/ㅈ 로 매핑됨)
    private final String alpha;     // A~Z (대소문자 무관)
    private final String symbol;    // ~@#$%&*/?-_+=.,!

    private final int page;
    private final int size;
    private final SortKey sortKey;
    private final org.springframework.data.domain.Sort.Direction direction;
    private final boolean includeTags;

    /** 편의 : prefix 모드 여부 */
    public boolean isPrefixMode() {
        int c = 0;
        if (initial != null) c++;
        if (alpha != null) c++;
        if (symbol != null) c++;
        return c == 1;
    }
}
