package com.wowraid.jobspoon.report.service.request;

import com.wowraid.jobspoon.report.entity.ReportStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateReportStatusRequest {
    private final ReportStatus status;
}
