package com.wowraid.jobspoon.term.service;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.term.service.request.SearchTermRequest;
import com.wowraid.jobspoon.term.service.response.SearchTermResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final TermRepository termRepository;

    @Override
    public SearchTermResponse search(SearchTermRequest request) {

        // 기본 = relevance
        boolean isRelevance = (request.getSortKey() == null) ||
                              (request.getSortKey() == SearchTermRequest.SortKey.RELEVANCE);

        Page<Term> page;

        if (isRelevance) {
            // 네이티브 쿼리 내부에서 score DESC, updated_at DESC 정렬을 처리하므로
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            page = termRepository.searchByRelevance(request.getQ(), request.isIncludeTags(), pageable);
        } else {
            // title/updatedAt 정렬만 허용(화이트리스트)
            Sort sort = (request.getSortKey() == SearchTermRequest.SortKey.TITLE)
                    ? Sort.by(request.getDirection(), "title")
                    : Sort.by(request.getDirection(), "updatedAt");
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            page = termRepository.searchLike(request.getQ(), request.isIncludeTags(), pageable);
        }

        List<SearchTermResponse.TermSummary> items = page.getContent().stream()
                .map(term -> SearchTermResponse.TermSummary.builder()
                        .id(term.getId())
                        .title(term.getTitle())
                        .description(term.getDescription())
                        .build())
                .toList();

        return SearchTermResponse.builder()
                .total(page.getTotalElements())
                .items(items)
                .build();
    }
}
