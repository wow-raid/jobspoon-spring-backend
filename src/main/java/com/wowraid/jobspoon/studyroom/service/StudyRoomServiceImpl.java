package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLevel;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomStatusRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public StudyRoom createStudyRoom(CreateStudyRoomRequestForm requestForm, Long hostId) {
        Account host = accountRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        StudyRoom studyRoom = StudyRoom.create(
                host,
                requestForm.getTitle(),
                requestForm.getDescription(),
                requestForm.getMaxMembers(),
                StudyLocation.valueOf(requestForm.getLocation().toUpperCase()),
                StudyLevel.valueOf(requestForm.getStudyLevel().toUpperCase()),
                requestForm.getRecruitingRoles(),
                requestForm.getSkillStack()
        );
        return studyRoomRepository.save(studyRoom);
    }

    public ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request) {
        Pageable pageable = PageRequest.of(0, request.getSize(), Sort.by("id").descending());

        Slice<StudyRoom> slice = (request.getLastStudyId() == null)
                ? studyRoomRepository.findAllByOrderByIdDesc(pageable)
                : studyRoomRepository.findByIdLessThanOrderByIdDesc(request.getLastStudyId(), pageable);

        // 👉 여기서 Entity → DTO(Map) 변환을 끝냄
        List<Map<String, Object>> studyRoomList = slice.getContent().stream()
                .map(room -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", room.getId());
                    map.put("title", room.getTitle());
                    map.put("status", room.getStatus().name());
                    map.put("location", room.getLocation().name());
                    map.put("studyLevel", room.getStudyLevel().name());
                    map.put("recruitingRoles", room.getRecruitingRoles());
                    map.put("skillStack", room.getSkillStack());
                    map.put("maxMembers", room.getMaxMembers());
                    return map;
                })
                .collect(Collectors.toList());

        return new ListStudyRoomResponse(studyRoomList, slice.hasNext());
    }

    @Override
    @Transactional
    public UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, UpdateStudyRoomRequest request) {
        // 수정할 스터디 모임을 찾음
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디모임입니다."));

        // 현재 로그인한 사용자 정보를 가져옴. (현재는 임시ID)
        Long currentUserId = 1L;
        Account currentUser = accountRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 로그인한 사용자가 수정 권한을 보유한 모임장인지 검사함.
        if (!studyRoom.getHost().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("수정 권한이 없는 사용자입니다.");
        }

        // 권한이 있다면 수정을 진행함.
        studyRoom.update(
                request.getTitle(),
                request.getDescription(),
                request.getMaxMembers(),
                request.getLocation(),
                request.getStudyLevel(),
                request.getRecruitingRoles(),
                request.getSkillStack()
        );

        return UpdateStudyRoomResponse.from(studyRoom);
    }

    @Override
    @Transactional
    public void updateStudyRoomStatus(Long studyRoomId, UpdateStudyRoomStatusRequest request) {
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디모임입니다."));

        // 현재 로그인한 사용자 정보를 가져옴. (현재는 임시ID)
        Long currentUserId = 1L;
        Account currentUser = accountRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 로그인한 사용자가 수정 권한을 보유한 모임장인지 검사함.
        if (!studyRoom.getHost().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("수정 권한이 없는 사용자입니다.");
        }
        studyRoom.updateStatus(request.getStatus());
    }

    @Override
    @Transactional
    public void deleteStudyRoom(Long studyRoomId, Long hostId) {
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 모임입니다."));

        // 권한 검사 (로그인한 사용자가 모임장인지)
        if (!studyRoom.getHost().getId().equals(hostId)) {
            throw new IllegalStateException("삭제 권한이 없는 사용자입니다.");
        }
        studyRoomRepository.delete(studyRoom);
    }
}