package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.QuizQuestion;
import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.service.response.InitialsQA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    @Query("""
           select q.id
           from QuizQuestion q
           where q.quizSet.id = :quizSetId
           order by coalesce(q.orderIndex, 999999), q.id
           """)
    List<Long> findIdsByQuizSetIdOrder(@Param("quizSetId") Long quizSetId);

    long countByQuizSet_Id(Long quizSetId);

    List<QuizQuestion> findByQuizSetIdAndQuestionTypeOrderByOrderIndexAscIdAsc(Long quizSetId, QuestionType questionType);
    List<QuizQuestion> findByQuizSet_IdOrderByOrderIndexAscIdAsc(Long setId);
}
