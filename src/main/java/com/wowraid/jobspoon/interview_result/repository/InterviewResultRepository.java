package com.wowraid.jobspoon.interview_result.repository;

import com.wowraid.jobspoon.interview_result.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
}
