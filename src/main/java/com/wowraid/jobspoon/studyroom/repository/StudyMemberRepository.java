package com.wowraid.jobspoon.studyroom.repository;

import com.wowraid.jobspoon.studyroom.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}
