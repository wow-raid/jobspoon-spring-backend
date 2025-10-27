package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.QuizPublication;
import com.wowraid.jobspoon.quiz.entity.enums.JobRole;
import com.wowraid.jobspoon.quiz.entity.enums.QuizPartType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QuizPublicationRepository extends JpaRepository<QuizPublication, Long> {
    Optional<QuizPublication> findFirstByScheduledDateAndPartTypeAndJobRoleAndActiveIsTrue(
            LocalDate date, QuizPartType part, JobRole role);
}

