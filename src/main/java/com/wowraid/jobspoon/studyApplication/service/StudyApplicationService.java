package com.wowraid.jobspoon.studyApplication.service;

import com.wowraid.jobspoon.studyApplication.service.request.CreateStudyApplicationRequest;
import com.wowraid.jobspoon.studyApplication.service.response.ApplicationForHostResponse;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import com.wowraid.jobspoon.studyApplication.service.response.ListMyApplicationResponse;
import com.wowraid.jobspoon.studyApplication.service.response.MyApplicationStatusResponse;

import java.util.List;

public interface StudyApplicationService {
    CreateStudyApplicationResponse applyToStudy(CreateStudyApplicationRequest request);

    List<ListMyApplicationResponse> findMyApplications(Long applicantId);

    void cancelApplication(Long applicationId, Long applicantId);

    MyApplicationStatusResponse findMyApplicationStatus(Long studyRoomId, Long applicantId);

    List<ApplicationForHostResponse> findApplicationsForHost(Long studyRoomId, Long hostId);

}
