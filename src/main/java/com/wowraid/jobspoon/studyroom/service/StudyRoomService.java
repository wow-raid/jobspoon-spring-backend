package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.request.*;
import com.wowraid.jobspoon.studyroom.service.response.*;

import java.util.List;

public interface StudyRoomService {
    CreateStudyRoomResponse createStudyRoom(CreateStudyRoomRequest request);

    ReadStudyRoomResponse readStudyRoom(Long studyRoomId, Long currentUserId);

    ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request);
    UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, Long currentUserId, UpdateStudyRoomRequest request);
    void updateStudyRoomStatus(Long studyRoomId, Long currentUserId, UpdateStudyRoomStatusRequest request);
    void deleteStudyRoom(Long studyRoomId, Long currentUserId);

    // 참여중인 면접스터디모임 목록
    List<MyStudyResponse> findMyStudies(Long currentUserId);

    // 면접스터디모임 내 참여인원 탭
    List<StudyMemberResponse> getStudyMembers(Long studyRoomId);

    String findUserRoleInStudyRoom(Long studyRoomId, Long currentUserId);

    // 면접스터디모임 참가자 탈퇴
    void leaveStudyRoom(Long studyRoomId, Long currentUserId);

    // 면접스터디모임 참가자 강퇴
    void kickMember(Long studyRoomId, Long memberIdToKick, Long leaderId);

    // 모의면접 채널 조회 메소드
    List<InterviewChannelResponse> findInterviewChannels(Long studyRoomId);

    // 모의면접 채널 수정 메소드
    void updateInterviewChannel(Long studyRoomId, Long leaderId, UpdateInterviewChannelRequest request);

    // 스터디 멤버 수에 따라 상태를 업데이트하는 매서드
    void updateStudyRoomStatusBasedOnMemberCount(StudyRoom studyRoom);
}