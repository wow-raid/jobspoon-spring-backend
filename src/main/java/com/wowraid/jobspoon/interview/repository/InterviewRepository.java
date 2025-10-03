package com.wowraid.jobspoon.interview.repository;

import com.wowraid.jobspoon.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}
