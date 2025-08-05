package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.UserQuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
}
