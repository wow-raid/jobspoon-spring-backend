package com.wowraid.jobspoon.term.controller.request_form;

import com.wowraid.jobspoon.term.service.request.ListTermRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ListTermRequestForm {

    private final Integer page = 1;
    private final Integer perPage = 10;
//    private final String query;     // 검색어 활용 시

    public ListTermRequest toListTermRequest() {
        return new ListTermRequest(page, perPage);
    }

}
