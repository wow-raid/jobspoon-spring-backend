package com.wowraid.jobspoon.user_dashboard.repository;

import com.wowraid.jobspoon.user_dashboard.entity.AttendanceDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceDayRepository extends JpaRepository<AttendanceDay, Long> {
    Optional<AttendanceDay> findByAccount_IdAndLoginDate(Long account_id, LocalDate login_date);

    @Query("""
        SELECT COUNT(a) FROM AttendanceDay a
        WHERE a.account.id = :accountId
          AND a.loginDate BETWEEN :start AND :end
    """)
    int countDaysInMonth(@Param("accountId") Long accountId,
                         @Param("start") LocalDate startInclusive,
                         @Param("end") LocalDate endInclusive);
}
