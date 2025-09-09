package com.wowraid.jobspoon.term.service;

import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.term.service.request.SearchTermRequest;
import com.wowraid.jobspoon.term.service.response.SearchTermResponse;
import com.wowraid.jobspoon.term.support.HangulInitial;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final TermRepository termRepository;

    @Override
    public SearchTermResponse search(SearchTermRequest request) {
        // 페이지/정렬 구성
        Pageable pageable = buildPageable(request);

        // 1) 접두(prefix) 모드 (initial | alpha | symbol 중 1개만 세팅)
        if (request.isPrefixMode()) {
            Page<Term> page = switchPrefix(request, pageable);
            return toResponse(page);
        }

        // 2) 기본 키워드 검색
        String q = (request.getQ() == null) ? "" : request.getQ().trim();
        if (q.isEmpty()) {
            return toResponse(Page.empty(pageable)); // 아무 조건도 없으면 빈 결과
        }

        // 정렬키가 RELEVANCE면 네이티브 점수 정렬 사용(내부에서 정렬 처리)
        if (request.getSortKey() == SearchTermRequest.SortKey.RELEVANCE) {
            Page<Term> page = termRepository.searchByRelevance(
                    q,
                    request.isIncludeTags(),
                    PageRequest.of(request.getPage(), request.getSize())
            );
            return toResponse(page);
        }

        // 그 외(title/updatedAt)는 JPQL LIKE + Pageable 정렬
        Page<Term> page = termRepository.searchLike(q, request.isIncludeTags(), pageable);
        return toResponse(page);
    }

    private Pageable buildPageable(SearchTermRequest request) {
        // RELEVANCE는 네이티브 쿼리에서 자체 정렬되므로 정렬 없이 페이지네이션만
        if (request.getSortKey() == SearchTermRequest.SortKey.RELEVANCE) {
            return PageRequest.of(request.getPage(), request.getSize());
        }
        Sort sort = switch (request.getSortKey()) {
            case TITLE      -> Sort.by(request.getDirection(), "title");
            case UPDATED_AT -> Sort.by(request.getDirection(), "updatedAt");
            default         -> Sort.unsorted();
        };
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private Page<Term> switchPrefix(SearchTermRequest request, Pageable pageable) {
        if (request.getInitial() != null) {
            String[] range = HangulInitial.range(request.getInitial());
            if (range == null) return Page.empty(pageable);
            return termRepository.findByHangulInitialRange(range[0], range[1], pageable);
        }
        if (request.getAlpha() != null) {
            return termRepository.findByFirstAlpha(request.getAlpha(), pageable);
        }
        if (request.getSymbol() != null) {
            return termRepository.findByFirstSymbol(request.getSymbol(), pageable);
        }
        return Page.empty(pageable);
    }

    private SearchTermResponse toResponse(Page<Term> page) {
        var items = page.getContent().stream()
                .map(t -> SearchTermResponse.TermSummary.builder()
                        .id(t.getId())
                        .title(t.getTitle())
                        .description(t.getDescription())
                        .build())
                .collect(Collectors.toList());

        return SearchTermResponse.builder()
                .total(page.getTotalElements())
                .items(items)
                .build();
    }
}
