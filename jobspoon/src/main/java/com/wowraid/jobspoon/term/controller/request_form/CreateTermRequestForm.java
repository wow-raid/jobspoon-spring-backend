package com.wowraid.jobspoon.term.controller.request_form;
// 용어 등록 (제목, 설명, 태그, 카테고리)

import com.wowraid.jobspoon.term.service.request.CreateTermRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateTermRequestForm {
    private final String title;
    private final String description;
    private final String tags;
//    Category category;
    private final String categoryId;

    public CreateTermRequest toCreateTermRequest() {
        return new CreateTermRequest(title, description, tags, categoryId);
    }
}
