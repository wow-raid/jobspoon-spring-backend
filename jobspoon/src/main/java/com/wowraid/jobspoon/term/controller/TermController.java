package com.wowraid.jobspoon.term.controller;

import com.wowraid.jobspoon.term.controller.request_form.CreateTermRequestForm;
import com.wowraid.jobspoon.term.controller.response_form.CreateTermResponseForm;
import com.wowraid.jobspoon.term.service.TermService;
import com.wowraid.jobspoon.term.service.response.CreateTermResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermController {

    private final TermService termService;

    // 용어 등록 (제목, 설명, 태그, 카테고리) title, description, categoryId, tags
    // 태그 문자열 파싱
    @PostMapping
    public CreateTermResponseForm createTerm (@RequestBody CreateTermRequestForm createTermRequestForm) {
        CreateTermResponse response = termService.register(createTermRequestForm.toCreateTermRequest());
        return CreateTermResponseForm.from(response);
    }

}
