package com.wowraid.jobspoon.studyroom.controller.request_Form;

import com.wowraid.jobspoon.studyroom.entity.StudyStatus;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomStatusRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomStatusRequestForm {
    private final String status;

    public UpdateStudyRoomStatusRequest toServiceRequest() {
        return new UpdateStudyRoomStatusRequest(StudyStatus.valueOf(status.toUpperCase()));
    }
}
