package com.wowraid.jobspoon.studyApplication.service.request;

import com.wowraid.jobspoon.studyApplication.entity.ApplicationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProcessApplicationRequest {
    private ApplicationStatus status;
}
