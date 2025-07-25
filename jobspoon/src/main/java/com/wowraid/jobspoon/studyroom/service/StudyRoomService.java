package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.RegisterStudyRoomRequestForm;
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

    @Transactional
    public List<RegisterStudyRoomResponse> findAllStudyRooms(){
        List<StudyRoom> studyRooms = studyRoomRepository.findAll();

        return studyRooms.stream()
                .map(RegisterStudyRoomResponse::from)
                .collect(Collectors.toList());
    }
}
