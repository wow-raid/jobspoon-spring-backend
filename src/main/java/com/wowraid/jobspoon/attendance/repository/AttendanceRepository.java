package com.wowraid.jobspoon.attendance.repository;

import com.wowraid.jobspoon.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByAccount_IdAndLoginDate(Long account_id, LocalDate login_date);

    @Query("""
        SELECT COUNT(a) FROM Attendance a
        WHERE a.account.id = :accountId
          AND a.loginDate BETWEEN :start AND :end
    """)
    int countDaysInMonth(@Param("accountId") Long accountId,
                         @Param("start") LocalDate startInclusive,
                         @Param("end") LocalDate endInclusive);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(
            value = "INSERT IGNORE INTO attendance (account_id, login_date)" +
                    "VALUES (:accountId, :loginDate)",
            nativeQuery = true
    )
    int insertIgnore(@Param("accountId") Long accountId, @Param("loginDate") LocalDate loginDate);
}