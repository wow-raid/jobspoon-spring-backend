package com.wowraid.jobspoon.term.controller.request_form;

import com.wowraid.jobspoon.term.service.request.UpdateTermRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 제목, 설명, 카테고리, 태그 수정 가능
@Getter
@RequiredArgsConstructor
public class UpdateTermRequestForm {

    private final Long termId;
    private final String title;
    private final String description;
    private final String tags;
    private final String categoryId;


    public UpdateTermRequest toUpdateTermRequest() {
        return new UpdateTermRequest(termId, title, description, tags, categoryId);
    }

}
