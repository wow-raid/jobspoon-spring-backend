package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;

import java.util.List;

public interface StudyRoomService {

    // 스터디룸 생성
    RegisterStudyRoomResponse createStudyRoom(RegisterStudyRoomRequestForm requestForm);

    // 스터디룸 전체조회
    List<RegisterStudyRoomResponse> findAllStudyRooms();

    // 스터디룸 지역별 조회
    List<RegisterStudyRoomResponse> findStudyRoomsByRegion(String region);

    // 스터디룸 수정
    RegisterStudyRoomResponse updateStudyRoom(Long studyRoomId, UpdateStudyRoomRequestForm requestForm);

    // 스터디룸 삭제
    void deleteStudyRoom(Long studyRoomId);
}
