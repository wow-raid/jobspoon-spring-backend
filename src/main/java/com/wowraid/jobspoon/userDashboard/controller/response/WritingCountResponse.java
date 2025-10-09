package com.wowraid.jobspoon.userDashboard.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WritingCountResponse {
    private long postCount;
    private long studyroomCount;
    private long commentCount;
    private long totalCount;
}
