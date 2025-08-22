package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;


public interface StudyRoomService {
    // Service가 Controller의 Form을 직접 받도록 하고, 생성된 Entity를 반환
    StudyRoom createStudyRoom(CreateStudyRoomRequestForm requestForm);
}