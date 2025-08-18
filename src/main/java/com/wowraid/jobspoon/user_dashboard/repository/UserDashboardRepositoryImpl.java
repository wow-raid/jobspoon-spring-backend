package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityAgg;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class UserDashboardRepositoryImpl implements UserDashboardRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public ActivityAgg summarize(Long accountId) {

        //실제 테이블, 컬럼명에 맞게 수정 필요
        String sql = """
            SELECT
              COALESCE((SELECT COUNT(DISTINCT DATE(al.attended_at))
                        FROM attendance_log al
                        WHERE al.account_id = ?1), 0) AS attendance_days,
              COALESCE((SELECT COUNT(*) FROM quiz_try qt WHERE qt.account_id = ?1), 0) AS question_tried,
              COALESCE((SELECT COUNT(*) FROM quiz_solve qs WHERE qs.account_id = ?1 AND qs.is_correct = 1), 0) AS question_solved,
              COALESCE((SELECT COUNT(*) FROM post p WHERE p.account_id = ?1), 0) AS posts,
              COALESCE((SELECT COUNT(*) FROM comment c WHERE c.account_id = ?1), 0) AS comments
            """;

        Object[] r = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, accountId)
                .getSingleResult();

        return new ActivityAgg(
                ((Number) r[0]).intValue(),
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityAgg summarizeRange(Long accountId, LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT
              COALESCE((
                SELECT COUNT(DISTINCT DATE(al.attended_at))
                FROM attendance_log al
                WHERE al.account_id = ?1
                  AND al.attended_at >= ?2 AND al.attended_at < ?3
              ), 0) AS attendance_days,
              COALESCE((
                SELECT COUNT(*)
                FROM quiz_try qt
                WHERE qt.account_id = ?1
                  AND qt.tried_at >= ?2 AND qt.tried_at < ?3
              ), 0) AS tried,
              COALESCE((
                SELECT COUNT(*)
                FROM quiz_solve qs
                WHERE qs.account_id = ?1 AND qs.is_correct = 1
                  AND qs.solved_at >= ?2 AND qs.solved_at < ?3
              ), 0) AS solved,
              COALESCE((
                SELECT COUNT(*)
                FROM post p
                WHERE p.account_id = ?1
                  AND p.created_at >= ?2 AND p.created_at < ?3
              ), 0) AS posts,
              COALESCE((
                SELECT COUNT(*)
                FROM comment c
                WHERE c.account_id = ?1
                  AND c.created_at >= ?2 AND c.created_at < ?3
              ), 0) AS comments
            """;

        Object[] r = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, accountId)
                .setParameter(2, from)
                .setParameter(3, to)
                .getSingleResult();

        return new ActivityAgg(
                ((Number) r[0]).intValue(),
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                ((Number) r[4]).longValue()
        );
    }
}