package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomStatusRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;


public interface StudyRoomService {
    // Service가 Controller의 Form을 직접 받도록 하고, 생성된 Entity를 반환
    StudyRoom createStudyRoom(CreateStudyRoomRequestForm requestForm, Long hostId);

    // 수정 매서드 시그니처 추가
    UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, UpdateStudyRoomRequest request);

    // status 수정 전용 매서드 시그니처
    void updateStudyRoomStatus(Long studyRoomId, UpdateStudyRoomStatusRequest request);

    ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request);
}