package com.wowraid.jobspoon.studyroom.repository;

import com.wowraid.jobspoon.studyroom.entity.Announcement;
import com.wowraid.jobspoon.studyroom.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Optional<StudyMember> findByStudyRoomIdAndAuthorId(Long studyRoomId, Long authorId);

    List<Announcement> findAllByStudyRoomId(Long studyRoomId);
}
