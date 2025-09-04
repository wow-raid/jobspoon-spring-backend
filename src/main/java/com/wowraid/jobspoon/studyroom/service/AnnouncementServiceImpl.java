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
import com.wowraid.jobspoon.studyroom.service.response.CreateAnnouncementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}