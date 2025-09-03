package com.wowraid.jobspoon.studyApplication.service.response;

import com.wowraid.jobspoon.studyApplication.entity.ApplicationStatus;
import com.wowraid.jobspoon.studyApplication.entity.StudyApplication;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateStudyApplicationResponse {
    private final Long applicationId;
    private final ApplicationStatus status;
    private final LocalDateTime appliedAt;

    public static CreateStudyApplicationResponse from(StudyApplication studyApplication) {
        return new CreateStudyApplicationResponse(
                studyApplication.getId(),
                studyApplication.getStatus(),
                studyApplication.getAppliedAt()
        );
    }
}
