package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.service.request.CreateStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomStatusRequest;
import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.ReadStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;

public interface StudyRoomService {
    CreateStudyRoomResponse createStudyRoom(CreateStudyRoomRequest request);
    ReadStudyRoomResponse readStudyRoom(Long studyRoomId);
    ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request);
    UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, Long currentUserId, UpdateStudyRoomRequest request);
    void updateStudyRoomStatus(Long studyRoomId, Long currentUserId, UpdateStudyRoomStatusRequest request);
    void deleteStudyRoom(Long studyRoomId, Long currentUserId);
}