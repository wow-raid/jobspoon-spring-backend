package com.wowraid.jobspoon.quiz.repository;

import com.wowraid.jobspoon.quiz.entity.SessionAnswer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionAnswerRepository extends CrudRepository<SessionAnswer, Long> {

    // 원본 세션에서 오답만 저장
    List<SessionAnswer> findByUserQuizSession_IdAndIsCorrectFalse(Long sessionId);

    // 성능/중복 제거 위해 questionId만 뽑는 버전
    @Query("""
        select distinct sa.quizQuestion.id
        from SessionAnswer sa
        where sa.userQuizSession.id = :sessionId
        and sa.isCorrect = false
    """)
    List<Long> findWrongQuestionIds(@Param("sessionId") Long sessionId);
}
