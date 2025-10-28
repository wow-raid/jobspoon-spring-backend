package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.UserWrongNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWrongNoteRepository extends JpaRepository<UserWrongNote, Long> {
    boolean existsByAccount_IdAndQuizQuestion_Id(Long accountId, Long quizQuestionId);
    Optional<UserWrongNote> findByAccount_IdAndQuizQuestion_Id(Long accountId, Long quizQuestionId);
}
