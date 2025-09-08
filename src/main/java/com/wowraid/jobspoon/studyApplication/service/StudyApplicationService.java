package com.wowraid.jobspoon.studyApplication.service;

import com.wowraid.jobspoon.studyApplication.service.request.CreateStudyApplicationRequest;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import com.wowraid.jobspoon.studyApplication.service.response.ListMyApplicationResponse;

import java.util.List;

public interface StudyApplicationService {
    CreateStudyApplicationResponse applyToStudy(CreateStudyApplicationRequest request);

    List<ListMyApplicationResponse> findMyApplications(Long applicantId);
}
