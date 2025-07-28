package com.wowraid.jobspoon.studyschedule.repository;

import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyScheduleRepository extends JpaRepository<StudySchedule,Long> {


}
