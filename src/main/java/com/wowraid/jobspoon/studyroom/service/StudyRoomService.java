package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;


public interface StudyRoomService {
    // Service가 Controller의 Form을 직접 받도록 하고, 생성된 Entity를 반환
    StudyRoom createStudyRoom(CreateStudyRoomRequestForm requestForm);

    ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request);
}