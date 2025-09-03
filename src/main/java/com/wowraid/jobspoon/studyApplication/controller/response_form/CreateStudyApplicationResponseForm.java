package com.wowraid.jobspoon.studyApplication.controller.response_form;

import com.wowraid.jobspoon.studyApplication.entity.ApplicationStatus;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateStudyApplicationResponseForm {
    private final Long applicationId;
    private final ApplicationStatus status;
    private final LocalDateTime appliedAt;

    public static CreateStudyApplicationResponseForm from(CreateStudyApplicationResponse response) {
        return new CreateStudyApplicationResponseForm(
                response.getApplicationId(),
                response.getStatus(),
                response.getAppliedAt()
        );
    }
}
