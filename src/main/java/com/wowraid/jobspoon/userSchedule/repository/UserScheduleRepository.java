package com.wowraid.jobspoon.userSchedule.repository;

import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    List<UserSchedule> findAllByAccountId(Long accountId);
    void deleteAllByAccountId(Long accountId);
}
