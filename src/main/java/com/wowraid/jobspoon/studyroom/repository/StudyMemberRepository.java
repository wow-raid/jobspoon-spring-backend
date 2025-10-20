package com.wowraid.jobspoon.studyroom.repository;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.studyroom.entity.StudyMember;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    @Query("SELECT DISTINCT sm FROM StudyMember sm " +
            "JOIN FETCH sm.studyRoom sr " +
            "JOIN FETCH sr.host " +
            // "LEFT JOIN FETCH sr.studyMembers " + // 👈 이 줄을 다시 삭제합니다.
            "LEFT JOIN FETCH sr.skillStack " +
            "LEFT JOIN FETCH sr.recruitingRoles " +
            "WHERE sm.accountProfile = :accountProfile")
    List<StudyMember> findByAccountProfileWithDetails(@Param("accountProfile") AccountProfile accountProfile);

    Optional<StudyMember> findByStudyRoomIdAndAccountProfileId(Long studyRoomId, Long accountProfileId);

    long countByStudyRoom(StudyRoom studyRoom);

    // 특정 스터디에 특정 사용자가 멤버로 존재하는지 확인하는 메서드
    boolean existsByStudyRoomIdAndAccountProfileId(Long studyRoomId, Long accountProfileId);

    void deleteAllByAccountProfileId(Long accountProfileId);
}