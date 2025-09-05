package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyroom.entity.Announcement;
import com.wowraid.jobspoon.studyroom.entity.StudyMember;
import com.wowraid.jobspoon.studyroom.entity.StudyRole;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.AnnouncementRepository;
import com.wowraid.jobspoon.studyroom.repository.StudyMemberRepository;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.request.CreateAnnouncementRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateAnnouncementRequest;
import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;
import com.wowraid.jobspoon.studyroom.service.response.ListAnnouncementResponse;
import com.wowraid.jobspoon.studyroom.service.response.ReadAnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Override
    @Transactional
    public CreateAnnouncementResponse createAnnouncement(CreateAnnouncementRequest request) {
        // 공지사항 생성이 가능한 사용자인지 권한검사 로직
        StudyMember member = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(
                request.getStudyRoomId(), request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디모임의 멤버가 아닙니다."));

        if (member.getRole() != StudyRole.LEADER) {
            throw new IllegalStateException("공지사항 작성 권한이 없습니다.");
        }

        // 권한검사 통과 시 공지사항 생성
        StudyRoom studyRoom = studyRoomRepository.findById(request.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));
        AccountProfile author = accountProfileRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Announcement announcement = Announcement.create(studyRoom, author, request.getTitle(), request.getContent());

        return CreateAnnouncementResponse.from(announcementRepository.save(announcement));
    }

    @Override
    public List<ListAnnouncementResponse> findAllAnnouncements(Long studyRoomId) { // ✅ 반환 타입 변경
        if (!studyRoomRepository.existsById(studyRoomId)) {
            throw new IllegalArgumentException("존재하지 않는 스터디룸입니다.");
        }

        return announcementRepository.findAllByStudyRoomId(studyRoomId)
                .stream()
                .map(ListAnnouncementResponse::from) // ✅ 엔티티를 새 Response 객체로 변환
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleAnnouncementPin(Long studyRoomId, Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));

        announcement.togglePin();
    }

    @Override
    public ReadAnnouncementResponse findAnnouncementById(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
        return ReadAnnouncementResponse.from(announcement);
    }

    @Override
    @Transactional
    public ReadAnnouncementResponse updateAnnouncement(Long announcementId, UpdateAnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
        announcement.update(request.getTitle(), request.getContent());
        return ReadAnnouncementResponse.from(announcement);
    }
}