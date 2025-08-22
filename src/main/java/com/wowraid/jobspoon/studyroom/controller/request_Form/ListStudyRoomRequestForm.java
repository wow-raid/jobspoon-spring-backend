package com.wowraid.jobspoon.studyroom.controller.request_Form;

import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ListStudyRoomRequestForm {
    private final Long lastStudyId;
    private final int size;

    public ListStudyRoomRequest toServiceRequest(){
        return new ListStudyRoomRequest(lastStudyId, size);
    }
}
