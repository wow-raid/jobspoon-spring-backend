package com.wowraid.jobspoon.user_dashboard.controller.response_form;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WritingCountResponse {
    private long reviewCount;
    private long studyroomcount;
    private long commentCount;
    private long totalCount;
}
