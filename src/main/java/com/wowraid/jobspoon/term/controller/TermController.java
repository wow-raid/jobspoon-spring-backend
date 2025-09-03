package com.wowraid.jobspoon.term.controller;

import com.wowraid.jobspoon.term.controller.request_form.CreateTermRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.ListTermRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.SearchRequestForm;
import com.wowraid.jobspoon.term.controller.request_form.UpdateTermRequestForm;
import com.wowraid.jobspoon.term.controller.response_form.CreateTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.ListTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.SearchTermResponseForm;
import com.wowraid.jobspoon.term.controller.response_form.UpdateTermResponseForm;
import com.wowraid.jobspoon.term.service.SearchService;
import com.wowraid.jobspoon.term.service.TermService;
import com.wowraid.jobspoon.term.service.request.ListTermRequest;
import com.wowraid.jobspoon.term.service.response.CreateTermResponse;
import com.wowraid.jobspoon.term.service.response.ListTermResponse;
import com.wowraid.jobspoon.term.service.response.UpdateTermResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermController {

    private final TermService termService;
    private final SearchService searchService;

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
    @GetMapping("/search")
    public SearchTermResponseForm search(@Valid @ModelAttribute SearchRequestForm requestForm) {
        return SearchTermResponseForm.from(
                searchService.search(requestForm.toRequest())
        );
    }
}
