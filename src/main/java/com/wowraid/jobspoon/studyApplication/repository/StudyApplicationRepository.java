package com.wowraid.jobspoon.studyApplication.repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.studyApplication.entity.StudyApplication;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyApplicationRepository extends JpaRepository<StudyApplication, Long> {

    // 특정 스터디와 지원자로 지원 내역이 존재하는지 확인(중복지원 방지용)
    boolean existsByStudyRoomAndApplicant(StudyRoom studyRoom, AccountProfile applicant);

    List<StudyApplication> findAllByApplicantId(Long applicantId);
}
