package com.wowraid.jobspoon.interview.repository;

import com.wowraid.jobspoon.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    @Query("SELECT i FROM Interview i WHERE i.account.id = :accountId AND i.deletedAt IS NULL")
    List<Interview> getInterviewResultListByAccountId(@Param("accountId") Long accountId);


}
