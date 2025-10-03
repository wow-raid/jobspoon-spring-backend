package com.wowraid.jobspoon.quiz.repository;

/*
user_dashboard에서 사용 예정
 */

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.quiz.entity.UserQuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserQuizSessionRepository extends JpaRepository<UserQuizSession, Long> {
    long countByAccountId(Long accountId);

    @Query("SELECT COUNT(u) FROM UserQuizSession u " +
            "WHERE u.account.id = :accountId " +
            "AND u.startedAt BETWEEN :start AND :end")
    long countMonthlyByAccountId(@Param("accountId") Long accountId,
                                 @Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    Optional<UserQuizSession> findByIdAndAccount_Id(Long id, Long accountId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserQuizSession s set s.sessionStatus = 'EXPIRED' " +
            "where s.id = :id and s.sessionStatus <> 'SUBMITTED' ")
    int expireIfNotSubmitted(@Param("id") Long id);

    Long account(Account account);
}