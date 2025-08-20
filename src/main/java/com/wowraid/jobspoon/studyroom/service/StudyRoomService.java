package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;

import java.util.List;

public interface StudyRoomService {

    // 스터디룸 생성
    CreateStudyRoomResponse createStudyRoom(CreateStudyRoomRequestForm requestForm);

    // 스터디룸 전체조회
    List<CreateStudyRoomResponse> findAllStudyRooms();

    // 스터디룸 지역별 조회
    List<CreateStudyRoomResponse> findStudyRoomsByRegion(String region);

    // 스터디룸 수정
    CreateStudyRoomResponse updateStudyRoom(Long studyRoomId, UpdateStudyRoomRequestForm requestForm);

    // 스터디룸 삭제
    void deleteStudyRoom(Long studyRoomId);
}
