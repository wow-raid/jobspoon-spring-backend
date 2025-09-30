package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyroom.entity.*;
import com.wowraid.jobspoon.studyroom.repository.InterviewChannelRepository;
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
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private final InterviewChannelRepository interviewChannelRepository;

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
    public ReadStudyRoomResponse readStudyRoom(Long studyRoomId, Long currentUserId) {
        log.info("--- [DEBUG] readStudyRoom 서비스 시작 ---");
        log.info("[DEBUG] 요청된 스터디 ID: {}", studyRoomId);
        log.info("[DEBUG] 현재 로그인된 사용자 ID: {}", currentUserId); // 🚨 이 값이 null인지 확인!
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        log.info("[DEBUG] DB에서 찾은 스터디의 모임장 ID: {}", studyRoom.getHost().getId());

        boolean isOwner = (currentUserId != null) && (currentUserId.equals(studyRoom.getHost().getId()));
        log.info("[DEBUG] isOwner 계산 결과: {}", isOwner); // 🚨 이 값이 true인지 확인!

        return ReadStudyRoomResponse.from(studyRoom, isOwner);
    }

    @Override
    public ListStudyRoomResponse findAllStudyRooms(ListStudyRoomRequest request) {
        Pageable pageable = PageRequest.of(0, request.getSize());

        Slice<Long> idSlice = (request.getLastStudyId() == null)
                ? studyRoomRepository.findIds(pageable)
                : studyRoomRepository.findIdsByIdLessThan(request.getLastStudyId(), pageable);

        List<Long> ids = idSlice.getContent();

        if (ids.isEmpty()) {
            // ✅ 비어있는 리스트로 SliceImpl 객체를 생성하여 반환
            return new ListStudyRoomResponse(new SliceImpl<>(Collections.emptyList()));
        }

        List<StudyRoom> studyRooms = studyRoomRepository.findAllWithDetailsByIds(ids);

        return new ListStudyRoomResponse(new SliceImpl<>(studyRooms, pageable, idSlice.hasNext()));
    }

    // 참여중인 면접스터디 목록 서비스 로직
    @Override
    @Transactional(readOnly = true)
    public List<MyStudyResponse> findMyStudies(Long currentUserId) { // 반환 타입 변경
        AccountProfile currentUser = accountProfileRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 프로필입니다."));

        List<StudyMember> myMemberships = studyMemberRepository.findByAccountProfileWithDetails(currentUser);

        // StudyRoom 엔티티 리스트를 직접 반환하도록 수정
        return myMemberships.stream()
                .map(StudyMember::getStudyRoom)
                .map(MyStudyResponse::from)
                .collect(Collectors.toList());
    }

    // 면접스터디모임 내 참여인원 탭 서비스 로직
    @Override
    public List<StudyMemberResponse> getStudyMembers(Long studyRoomId) {
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디모임 입니다."));

        return studyRoom.getStudyMembers().stream()
                .map(StudyMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String findUserRoleInStudyRoom(Long studyRoomId, Long accountProfileId) {
        return studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, accountProfileId)
                .map(studyMember -> studyMember.getRole().name()) // "LEADER" or "MEMBER"
                .orElseThrow(() -> new IllegalStateException("해당 스터디룸에 가입되지 않은 사용자입니다."));
    }

    @Override
    @Transactional
    public UpdateStudyRoomResponse updateStudyRoom(Long studyRoomId, Long currentUserId, UpdateStudyRoomRequest request) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (studyRoom.getStatus() == StudyStatus.CLOSED) {
            throw new IllegalStateException("폐쇄된 스터디모임은 수정할 수 없습니다.");
        }

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

    // 모임장의 스터디모임 폐쇄 시 DB에서 삭제하는 것이 아닌 폐쇄 형태로 접근을 제한함
    @Override
    @Transactional
    public void deleteStudyRoom(Long studyRoomId, Long currentUserId) {
        StudyRoom studyRoom = studyRoomRepository.findByIdWithHost(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (!studyRoom.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없는 사용자입니다.");
        }
        studyRoom.updateStatus(StudyStatus.CLOSED);
    }

    @Override
    @Transactional
    public void leaveStudyRoom(Long studyRoomId, Long currentUserId) {
        StudyMember member = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버 정보를 찾을 수 없습니다."));

        if (member.getRole() == StudyRole.LEADER) {
            throw new IllegalStateException("리더는 스터디를 탈퇴할 수 없습니다. 스터디를 폐쇄해야 합니다.");
        }

        StudyRoom studyRoom = member.getStudyRoom();
        studyMemberRepository.delete(member);

        // ✅ [수정] 서비스 내의 헬퍼 메소드를 호출
        this.updateStudyRoomStatusBasedOnMemberCount(studyRoom);
    }

    @Override
    @Transactional
    public void kickMember(Long studyRoomId, Long memberIdToKick, Long leaderId) {
        StudyMember leader = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, leaderId)
                .orElseThrow(() -> new IllegalArgumentException("리더 정보를 찾을 수 없습니다."));

        if (leader.getRole() != StudyRole.LEADER) {
            throw new IllegalStateException("리더만 멤버를 강퇴할 수 있습니다.");
        }
        if (leaderId.equals(memberIdToKick)) {
            throw new IllegalStateException("리더는 자신을 강퇴할 수 없습니다.");
        }

        StudyMember memberToKick = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, memberIdToKick)
                .orElseThrow(() -> new IllegalArgumentException("강퇴할 멤버 정보를 찾을 수 없습니다."));

        StudyRoom studyRoom = memberToKick.getStudyRoom();
        studyMemberRepository.delete(memberToKick);

        // ✅ [수정] 서비스 내의 헬퍼 메소드를 호출
        this.updateStudyRoomStatusBasedOnMemberCount(studyRoom);
    }

    private void updateStudyRoomStatusBasedOnMemberCount(StudyRoom studyRoom) {
        long currentMemberCount = studyMemberRepository.countByStudyRoom(studyRoom);

        // ✅ [수정] 인원이 꽉 차면 '모집완료(COMPLETED)' 상태로 변경
        if (currentMemberCount >= studyRoom.getMaxMembers()) {
            studyRoom.updateStatus(StudyStatus.COMPLETED);
        } else {
            // ✅ [수정] 모집완료 상태에서도 인원이 줄면 다시 모집중으로 변경
            if (studyRoom.getStatus() == StudyStatus.COMPLETED) {
                studyRoom.updateStatus(StudyStatus.RECRUITING);
            }
        }
    }
    // 모의면접 채널 조회 (Create 로직 포함)
    @Override
    @Transactional
    public List<InterviewChannelResponse> findInterviewChannels(Long studyRoomId) {
        List<InterviewChannel> channels = interviewChannelRepository.findAllByStudyRoomId(studyRoomId);

        // CREATE
        if (channels.isEmpty()) {
            StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

            List<String> defaultChannelNames = List.of("Kakao", "Google", "Zoom", "Discord", "Naver");

            channels = defaultChannelNames.stream()
                    .map(name -> InterviewChannel.create(studyRoom, name))
                    .collect(Collectors.toList());

            interviewChannelRepository.saveAll(channels);
        }

        // READ
        return channels.stream()
                .map(InterviewChannelResponse::from)
                .collect(Collectors.toList());
    }
}