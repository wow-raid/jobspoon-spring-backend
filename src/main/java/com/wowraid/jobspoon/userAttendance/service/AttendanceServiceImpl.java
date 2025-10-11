package com.wowraid.jobspoon.userAttendance.service;

import com.wowraid.jobspoon.userDashboard.controller.response.AttendanceRateResponse;
import com.wowraid.jobspoon.userAttendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceDayRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 로그인 성공 시 호출 : 오늘 로그인했는가? 하루 1회만 기록 **/
    @Override
    @Transactional
    public boolean markLogin(Long accountId) {
        LocalDate todayKst = LocalDate.now(KST);

        int rows = attendanceDayRepository.insertIgnore(accountId, todayKst);
        return rows > 0; // 1이면 새로 찍힘, 0이면 이미 있었음
    }

    /** 이번 달 출석률(%) - 달력 기준 **/
    @Override
    @Transactional(readOnly = true)
    public AttendanceRateResponse getThisMonthRate(Long accountId) {

        YearMonth yearMonth = YearMonth.now(KST);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int attended = attendanceDayRepository.countDaysInMonth(accountId, startDate, endDate);
        int total = yearMonth.lengthOfMonth();

        double attendanceRate = (attended * 100.0) / total;

        return new AttendanceRateResponse(attendanceRate, attended, total);
    }
}
