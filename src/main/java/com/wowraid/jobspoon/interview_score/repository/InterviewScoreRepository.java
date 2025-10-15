package com.wowraid.jobspoon.interview_score.repository;

import com.wowraid.jobspoon.interview_score.entity.InterviewScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewScoreRepository extends JpaRepository<InterviewScore, Long> {
}
