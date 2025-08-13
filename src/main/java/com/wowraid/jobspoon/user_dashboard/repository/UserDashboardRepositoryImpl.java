package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.dto.ActivityAgg;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDashboardRepositoryImpl implements UserDashboardRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public ActivityAgg summarize(Long accountId) {

        //실제 테이블, 컬럼명에 맞게 수정 필요
        String sql = """
            SELECT
              COALESCE((SELECT COUNT(DISTINCT DATE(al.attended_at))
                        FROM attendance_log al
                        WHERE al.account_id = :accountId), 0) AS attendance_days,
              COALESCE((SELECT COUNT(*)
                        FROM quiz_try qt
                        WHERE qt.account_id = :accountId), 0) AS question_tried,
              COALESCE((SELECT COUNT(*)
                        FROM quiz_solve qs
                        WHERE qs.account_id = :accountId
                          AND qs.is_correct = 1), 0) AS question_solved,
              COALESCE((SELECT COUNT(*)
                        FROM post p
                        WHERE p.account_id = :accountId), 0) AS posts,
              COALESCE((SELECT COUNT(*)
                        FROM comment c
                        WHERE c.account_id = :accountId), 0) AS comments
        """;

        Object[] r = (Object[]) em.createNativeQuery(sql)
                .setParameter("accountId", accountId)
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