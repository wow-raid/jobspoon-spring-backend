package com.wowraid.jobspoon.user_term.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FolderSummaryResponseForm {
    private final Long id;
    private final String name;
    private final Long termCount;   // 폴더 내 즐겨찾기 용어 수
}
