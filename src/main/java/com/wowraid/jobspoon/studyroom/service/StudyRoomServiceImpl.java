package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.response.RegisterStudyRoomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    // 생성 서비스
    @Override
    @Transactional
    public RegisterStudyRoomResponse createStudyRoom(RegisterStudyRoomRequestForm requestForm){
        StudyRoom newStudyRoom = StudyRoom.create(
            requestForm.getStudyTitle(),
            requestForm.getDescription(),
            requestForm.getMaxMembers(),
            requestForm.getStatus(),
            requestForm.getRegion(),
            requestForm.getChatLink()
);
        StudyRoom savedStudyRoom = studyRoomRepository.save(newStudyRoom);
        return RegisterStudyRoomResponse.from(savedStudyRoom);
    }

    // 전체조회 서비스
    @Override
    @Transactional
    public List<RegisterStudyRoomResponse> findAllStudyRooms(){
        List<StudyRoom> studyRooms = studyRoomRepository.findAll();

        return studyRooms.stream()
                .map(RegisterStudyRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 필터조회 서비스
    @Override
    @Transactional
    public List<RegisterStudyRoomResponse> findStudyRoomsByRegion(String region){
        return studyRoomRepository.findByRegion(region).stream()
                .map(RegisterStudyRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 수정 서비스
    @Override
    @Transactional
    public RegisterStudyRoomResponse updateStudyRoom(Long studyRoomId, UpdateStudyRoomRequestForm requestForm){
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디룸을 찾을 수 없습니다."));

        studyRoom.update(
                requestForm.getStudyTitle(),
                requestForm.getDescription(),
                requestForm.getMaxMembers(),
                requestForm.getStatus(),
                requestForm.getRegion(),
                requestForm.getChatLink()
        );
        return RegisterStudyRoomResponse.from(studyRoom);
    }

    // 삭제 서비스
    @Override
    @Transactional
    public void deleteStudyRoom(Long studyRoomId){ // 추후 Account currentUser 추가해야하리보
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디를 찾을 수 없습니다. id=" + studyRoomId));
        studyRoomRepository.delete(studyRoom);
    }
}