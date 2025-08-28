package com.wowraid.jobspoon.studyroom.repository;

import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    @Query("SELECT sr FROM StudyRoom sr JOIN FETCH sr.host WHERE sr.id = :id")
    Optional<StudyRoom> findByIdWithHost(@Param("id") Long id);

    // ID 기반 커서 페이징 쿼리
    Slice<StudyRoom> findByIdLessThanOrderByIdDesc(Long lastStudyId, Pageable pageable);

    // 👇 최초 페이지 조회를 위한 메서드를 추가합니다.
    Slice<StudyRoom> findAllByOrderByIdDesc(Pageable pageable);

    List<StudyRoom> findByLocation(StudyLocation location);
}