package com.wowraid.jobspoon.term.controller;

import com.wowraid.jobspoon.term.controller.request_form.CreateTermRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.ListTermRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.SearchRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.UpdateTermRequestForm;
import com.wowraid.jobspoon.term.controller.response_form.CreateTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.ListTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.SearchTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.UpdateTermResponseForm;
import com.wowraid.jobspoon.term.entity.Term;
import com.wowraid.jobspoon.term.repository.TermRepository;
import com.wowraid.jobspoon.term.repository.TermTagRepository;
import com.wowraid.jobspoon.term.service.SearchService;
import com.wowraid.jobspoon.term.service.TagTextReader;
import com.wowraid.jobspoon.term.service.TermService;
import com.wowraid.jobspoon.term.service.request.ListTermRequest;
import com.wowraid.jobspoon.term.service.response.CreateTermResponse;
import com.wowraid.jobspoon.term.service.response.ListTermResponse;
import com.wowraid.jobspoon.term.service.response.UpdateTermResponse;
import com.wowraid.jobspoon.term.support.TagTextParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermController {

    private final TermService termService;
    private final SearchService searchService;
    private final TermTagRepository termTagRepository;
    private final TagTextReader tagTextReader;

    // 용어 등록 (제목, 설명, 태그, 카테고리) title, description, categoryId, tags
    // 태그 문자열 파싱
    @PostMapping
    public CreateTermResponseForm createTerm (@RequestBody CreateTermRequestForm createTermRequestForm) {
        CreateTermResponse response = termService.register(createTermRequestForm.toCreateTermRequest());
        return CreateTermResponseForm.from(response);
    }

    // PUT /api/terms/{termId} — 용어 정보 수정
    @PutMapping("/{termId}")
    public UpdateTermResponseForm updateTerm (
            @PathVariable Long termId,
            @RequestBody UpdateTermRequestForm updateTermRequestForm) {
        UpdateTermResponse response = termService.updateTerm(updateTermRequestForm.toUpdateTermRequest(termId));
        return UpdateTermResponseForm.from(response);
    }

    // DELETE /api/terms/{termId} — 용어 삭제
    @DeleteMapping("/{termId}")
    public ResponseEntity<String> deleteTerm (@PathVariable Long termId) {
        termService.deleteTerm(termId);
        return ResponseEntity.ok("용어가 성공적으로 삭제되었습니다.");
    }

    // 모든 용어를 페이지 단위로 확인하기
    @GetMapping
    public ListTermResponseForm termList(@ModelAttribute ListTermRequestForm requestForm) {
        log.info("termList() -> requestForm: {}", requestForm);

        ListTermRequest request = requestForm.toListTermRequest();
        ListTermResponse response = termService.list(request);

        return ListTermResponseForm.from(response);
    }
    
    // '가장 일치하는 제목' 기준으로 검색 결과 반환하기
    @CrossOrigin(origins = {"http://localhost:3006"})
    @GetMapping("/search")
    public SearchTermResponseForm search(@Valid @ModelAttribute SearchRequestForm requestForm) {
        return SearchTermResponseForm.from(
                searchService.search(requestForm.toRequest())
        );
    }

    // 단건 태그 조회
    @CrossOrigin(origins = {"http://localhost:3006"})
    @GetMapping("/{termId}/tags")
    public List<String> tags(@PathVariable Long termId) {
        // 1) 정규화 테이블 조인
        List<String> names = termTagRepository.findAllNamesByTermId(termId);
        if (!names.isEmpty()) return names;

        // 2) 폴백: term 테이블의 태그 문자열에서 파싱 (동적 컬럼 탐지)
        String raw = tagTextReader.readRaw(termId).orElse(null);
        List<String> parsed = TagTextParser.parse(raw);
        log.debug("[tags] termId={} joinSize={} parsed={}", termId, names.size(), parsed);
        return parsed;
    }

    // 배치 태그 조회: /api/terms/tags?ids=1&ids=2...
    @CrossOrigin(origins = {"http://localhost:3006"})
    @GetMapping("/tags")
    public Map<Long, List<String>> tagsByIds(@RequestParam("ids") List<Long> ids) {
        // 1) 정규화 테이블에서 최대한 수집
        var rows = termTagRepository.findTermIdAndTagNameByTermIdIn(ids);
        Map<Long, List<String>> map = new LinkedHashMap<>();
        for (var r : rows) {
            map.computeIfAbsent(r.getTermId(), k -> new ArrayList<>()).add(r.getTagName());
        }

        // 2) 비어있는 항목은 폴백으로 텍스트 파싱
        for (Long id : ids) {
            if (!map.containsKey(id) || map.get(id).isEmpty()) {
                String raw = tagTextReader.readRaw(id).orElse(null);
                List<String> parsed = TagTextParser.parse(raw);
                if (!parsed.isEmpty()) map.put(id, parsed);
            } else {
                // (선택) 조인 결과와 텍스트 결과를 합치고 중복 제거
                String raw = tagTextReader.readRaw(id).orElse(null);
                List<String> parsed = TagTextParser.parse(raw);
                if (!parsed.isEmpty()) {
                    Set<String> merged = new LinkedHashSet<>(map.get(id));
                    merged.addAll(parsed);
                    map.put(id, new ArrayList<>(merged));
                }
            }
        }

        // 3) 정렬 + 중복 제거
        map.replaceAll((k, v) -> v.stream().filter(Objects::nonNull).distinct().sorted().toList());
        return map;
    }

    // 연관 키워드(해시태그) 클릭 시 같은 태그의 용어만 조회하기
    @CrossOrigin(origins = {"http://localhost:3006"})
    @GetMapping("/search/by-tag")
    public ListTermResponseForm searchByTag(
            @RequestParam String tag,
            @RequestParam(defaultValue ="1") int page,
            @RequestParam(defaultValue = "10") int size) {
        ListTermResponse response = termService.searchByTag(tag, page, size);

        // termId들 뽑아서 태그 조회
        List<Long> ids = response.getTermList().stream().map(Term::getId).toList();
        Map<Long, List<String>> tagMap = new HashMap<>();
        if (!ids.isEmpty()) {
            var rows = termTagRepository.findTermIdAndTagNameByTermIdIn(ids);
            for(var r : rows) {
                tagMap.computeIfAbsent(r.getTermId(), k -> new ArrayList<>()).add(r.getTagName());
            }
            // 중복 제거 + 정렬
            tagMap.replaceAll((k, v) -> v.stream().distinct().sorted().toList());
        }
        response.setTagsByTermId(tagMap);
        return ListTermResponseForm.from(response);
    }
}
