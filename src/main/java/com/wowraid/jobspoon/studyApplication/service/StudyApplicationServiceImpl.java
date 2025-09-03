package com.wowraid.jobspoon.studyApplication.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyApplication.entity.StudyApplication;
import com.wowraid.jobspoon.studyApplication.repository.StudyApplicationRepository;
import com.wowraid.jobspoon.studyApplication.service.request.CreateStudyApplicationRequest;
import com.wowraid.jobspoon.studyApplication.service.response.CreateStudyApplicationResponse;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudyApplicationServiceImpl implements StudyApplicationService {

    private final StudyApplicationRepository studyApplicationRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final AccountProfileRepository accountProfileRepository;

    @Override
    public CreateStudyApplicationResponse applyToStudy(CreateStudyApplicationRequest request) {
        log.info("applyToStudy 서비스 시작. studyRoomId={}, applicantId={}, message='{}'",
                request.getStudyRoomId(), request.getApplicantId(), request.getMessage());

        AccountProfile applicant = accountProfileRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지원자를 찾을 수 없습니다. ID: " + request.getApplicantId()));

        StudyRoom studyRoom = studyRoomRepository.findById(request.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디모임을 찾을 수 없습니다. ID: " + request.getStudyRoomId()));

        if (studyRoom.getHost().getId().equals(applicant.getId())) {
            throw new IllegalStateException("모임장은 자신의 스터디에 지원할 수 없습니다.");
        }

        boolean isAlreadyApplied = studyApplicationRepository.existsByStudyRoomAndApplicant(studyRoom, applicant);
        if (isAlreadyApplied) {
            throw new IllegalStateException("이미 해당 스터디모임에 지원했습니다.");
        }

        StudyApplication studyApplication = StudyApplication.create(
                studyRoom,
                applicant,
                request.getMessage()
        );

        StudyApplication savedApplication = studyApplicationRepository.save(studyApplication);

        return CreateStudyApplicationResponse.from(savedApplication);
    }
}
