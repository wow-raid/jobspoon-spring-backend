package com.wowraid.jobspoon.interview.repository;

import com.wowraid.jobspoon.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findTopByAccountIdAndIsFinishedTrueOrderByCreatedAtDesc(Long accountId);
}
