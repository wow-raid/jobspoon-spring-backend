package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyroom.entity.*;
import com.wowraid.jobspoon.studyroom.repository.StudyMemberRepository;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.request.*;
import com.wowraid.jobspoon.studyroom.service.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyRoomServiceImpl implements StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Override
    @Transactional
    public CreateStudyRoomResponse createStudyRoom(CreateStudyRoomRequest request) {
            AccountProfile host = accountProfileRepository.findById(request.getHostId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 프로필입니다."));

            StudyRoom studyRoom = StudyRoom.create(
                    host,
                    request.getTitle(),
                    request.getDescription(),
                    request.getMaxMembers(),
                    request.getLocation(),
                    request.getStudyLevel(),
                    request.getRecruitingRoles(),
                    request.getSkillStack()
            );
            StudyRoom savedStudyRoom = studyRoomRepository.save(studyRoom);

            StudyMember studyHost = StudyMember.create(savedStudyRoom, host, StudyRole.LEADER);
            studyMemberRepository.save(studyHost);
            return CreateStudyRoomResponse.from(savedStudyRoom);
        }

    @Override
    public ReadStudyRoomResponse readStudyRoom(Long studyRoomId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));
        return ReadStudyRoomResponse.from(studyRoom);
    }

    @Override
    public ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request) {
        Pageable pageable = PageRequest.of(0, request.getSize());
        Slice<StudyRoom> slice = (request.getLastStudyId() == null)
                ? studyRoomRepository.findAllByOrderByIdDesc(pageable)
                : studyRoomRepository.findByIdLessThanOrderByIdDesc(request.getLastStudyId(), pageable);
        return new ListStudyRoomResponse(slice);
    }

    // 참여중인 면접스터디 목록 서비스 로직
    @Override
    @Transactional(readOnly = true)
    public List<StudyRoom> findMyStudies(Long currentUserId) { // 반환 타입 변경
        AccountProfile currentUser = accountProfileRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 프로필입니다."));

        List<StudyMember> myMemberships = studyMemberRepository.findByAccountProfileWithDetails(currentUser);

        // StudyRoom 엔티티 리스트를 직접 반환하도록 수정
        return myMemberships.stream()
                .map(StudyMember::getStudyRoom)
                .collect(Collectors.toList());
    }

    // 면접스터디모임 내 참여인원 탭 서비스 로직
    @Override
    public List<StudyMemberResponse> getStudyMembers(Long studyRoomId){
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디모임 입니다."));

        return studyRoom.getStudyMembers().stream()
                .map(StudyMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, Long currentUserId, UpdateStudyRoomRequest request) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (!studyRoom.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("수정 권한이 없는 사용자입니다.");
        }

        studyRoom.update(
                request.getTitle(), request.getDescription(), request.getMaxMembers(),
                request.getLocation(), request.getStudyLevel(),
                request.getRecruitingRoles(), request.getSkillStack()
        );
        return UpdateStudyRoomResponse.from(studyRoom);
    }

    @Override
    @Transactional
    public void updateStudyRoomStatus(Long studyRoomId, Long currentUserId, UpdateStudyRoomStatusRequest request) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (!studyRoom.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("수정 권한이 없는 사용자입니다.");
        }
        studyRoom.updateStatus(request.getStatus());
    }

    @Override
    @Transactional
    public void deleteStudyRoom(Long studyRoomId, Long currentUserId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (!studyRoom.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없는 사용자입니다.");
        }
        studyRoomRepository.delete(studyRoom);
    }

    @Override
    @Transactional
    public void leaveStudyRoom(Long studyRoomId, Long currentUserId) {
        StudyMember member = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 정보를 찾을 수 없습니다."));

        if (member.getRole() == StudyRole.LEADER) {
            throw new IllegalStateException("리더는 스터디를 탈퇴할 수 없습니다. 스터디를 폐쇄해야 합니다.");
        }
        studyMemberRepository.delete(member);
    }
}