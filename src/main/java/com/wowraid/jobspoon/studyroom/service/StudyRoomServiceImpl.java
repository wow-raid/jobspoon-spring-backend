package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
//    private final AccountService accountService;

    @Override
    @Transactional
    public StudyRoom createStudyRoom(CreateStudyRoomRequestForm requestForm) {
//        Long hostId = 1L;
//        Account host = accountService.findAccountById(hostId);

        StudyRoom studyRoom = StudyRoom.create(
//                host,         // 추후에 accountService 만들어지면 null 삭제하고 주석 해제
                null,
                requestForm.getTitle(),
                requestForm.getDescription(),
                requestForm.getMaxMembers(),
                StudyLocation.valueOf(requestForm.getLocation().toUpperCase()),
                requestForm.getRecruitingRoles(),
                requestForm.getSkillStack()
        );
        return studyRoomRepository.save(studyRoom);
    }

    @Override
    @Transactional
    public ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request) {
        Pageable pageable = PageRequest.of(0, request.getSize(), Sort.by("id").descending());
        if (request.getLastStudyId() == null){
            // 최초 요청 시에는 ID 조건 없이 조회함
            return new ListStudyRoomResponse(studyRoomRepository.findAllByOrderByIdDesc(pageable));
        }
        // 다음 페이지 요청 시, 마지막 ID와 Pageable을 함께 전달함
        return new ListStudyRoomResponse(
                studyRoomRepository.findByIdLessThanOrderByIdDesc(request.getLastStudyId(), pageable)
        );
    }
}