package com.wowraid.jobspoon.term.controller.request_form;

import com.wowraid.jobspoon.term.service.request.SearchTermRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.domain.Sort;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestForm {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    /** 검색어 */
    @NotBlank
    @Size(min = 1, max = 100)
    private String q;

    /** 페이지(0-base) */
    @Min(0)
    private Integer page;

    /** 페이지 크기 */
    @Min(1) @Max(MAX_SIZE)
    private Integer size;

    /**
     * 정렬 파라미터(화이트리스트 파싱)
     * - 허용: relevance,desc | title,asc|desc | updatedAt,desc|asc
     * - JSAB-39 기본값: relevance,desc
     */
    private String sort;

    /** 태그 포함 검색 여부(선택) */
    private Boolean includeTags;

    public SearchTermRequest toRequest() {
        // 1) 기본값/정규화
        final String trimmed = q == null ? "" : q.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("q must not be blank");

        final int p = (page == null || page < 0) ? DEFAULT_PAGE : page;
        final int s = (size == null) ? DEFAULT_SIZE : Math.min(Math.max(size, 1), MAX_SIZE);
        final boolean withTags = Boolean.TRUE.equals(includeTags);

        // 2) 화이트리스트 파싱: <key>,<direction>
        final String sortParam = (sort == null || sort.isBlank()) ? "relevance,desc" : sort.trim();
        String[] parts = sortParam.split("\\s*,\\s*");
        final String key = parts.length > 0 ? parts[0].toLowerCase() : "relevance";
        final String dir = parts.length > 1 ? parts[1].toLowerCase() : "desc";

        final SearchTermRequest.SortKey sortKey = switch (key) {
            case "relevance" -> SearchTermRequest.SortKey.RELEVANCE;
            case "title"     -> SearchTermRequest.SortKey.TITLE;
            case "updatedat" -> SearchTermRequest.SortKey.UPDATED_AT;
            default          -> throw new IllegalArgumentException("Invalid sort key: " + key);
        };
        final Sort.Direction direction = switch (dir) {
            case "asc"  -> Sort.Direction.ASC;
            case "desc" -> Sort.Direction.DESC;
            default     -> throw new IllegalArgumentException("Invalid sort direction: " + dir);
        };

        // 3) 서비스 DTO로 변환
        return SearchTermRequest.builder()
                .q(trimmed)
                .page(p)
                .size(s)
                .sortKey(sortKey)
                .direction(direction)
                .includeTags(withTags)
                .build();
    }
}
