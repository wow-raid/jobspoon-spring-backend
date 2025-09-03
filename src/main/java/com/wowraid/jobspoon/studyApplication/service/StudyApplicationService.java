package com.wowraid.jobspoon.studyApplication.service;

import com.wowraid.jobspoon.studyApplication.service.request.CreateStudyApplicationRequest;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;

public interface StudyApplicationService {
    CreateStudyApplicationResponse applyToStudy(CreateStudyApplicationRequest request);
}
