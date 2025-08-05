package com.wowraid.jobspoon.studyschedule.repository;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyScheduleRepository extends JpaRepository<StudySchedule,Long> {

    List<StudySchedule> findAllByStudyRoomId(Long studyRoomId);

}
