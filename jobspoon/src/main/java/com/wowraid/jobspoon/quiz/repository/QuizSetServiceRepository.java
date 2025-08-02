package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.term.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface QuizSetServiceRepository extends JpaRepository<QuizSet, Long> {
    Optional<QuizSet> findByTitle(String title);

    @Query("SELECT q FROM QuizQuestion q WHERE q.category = :category")
    List<QuizQuestion> findByCategory(Category category);
}
