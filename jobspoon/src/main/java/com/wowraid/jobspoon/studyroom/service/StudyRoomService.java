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
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    // 이건 생성이에옹
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

    // 이건 전체조회에옹
    @Transactional
    public List<RegisterStudyRoomResponse> findAllStudyRooms(){
        List<StudyRoom> studyRooms = studyRoomRepository.findAll();

        return studyRooms.stream()
                .map(RegisterStudyRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 이건 필터조회에옹
    @Transactional
    public List<RegisterStudyRoomResponse> findStudyRoomsByRegion(String region){
        return studyRoomRepository.findByRegion(region).stream()
                .map(RegisterStudyRoomResponse::from)
                .collect(Collectors.toList());
    }

    // 이건 수정이다람쥐?
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
}
