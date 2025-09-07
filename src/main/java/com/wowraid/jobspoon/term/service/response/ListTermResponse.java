package com.wowraid.jobspoon.term.service.response;

import com.wowraid.jobspoon.term.entity.Term;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ListTermResponse {
    private final List<Term> termList;
    private final Long totalItems;
    private final Integer totalPages;

    public List<Map<String, Object>> transformToResponseForm() {
        return termList.stream()
                .map(term -> {
                    Map<String, Object> termMap = new HashMap<>();
                    termMap.put("id", term.getId());
                    termMap.put("title", term.getTitle());
                    termMap.put("description", term.getDescription());
                    termMap.put("category", term.getCategory() != null ? term.getCategory().getName() : null);
                    return termMap;
                })
                .collect(Collectors.toList());
    }

    public static ListTermResponse from(final Page<Term> paginatedTerm) {
        return new ListTermResponse(
                paginatedTerm.getContent(),
                paginatedTerm.getTotalElements(),
                paginatedTerm.getTotalPages()
        );
    }

    public static ListTermResponse empty() {
        return new ListTermResponse(
                List.of(),  // termList(빈 리스트)
                0L,         // totalItems
                0           // totalPages
        );
    }
}
