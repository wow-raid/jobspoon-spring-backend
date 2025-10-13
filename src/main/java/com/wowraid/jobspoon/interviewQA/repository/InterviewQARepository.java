package com.wowraid.jobspoon.interviewQA.repository;

import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewQARepository extends JpaRepository<InterviewQA, Long> {
    List<InterviewQA> findByInterview_Id(Long interviewId);
}
