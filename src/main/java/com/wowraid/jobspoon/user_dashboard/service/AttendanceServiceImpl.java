package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.user_dashboard.controller.response_form.AttendanceRateResponse;
import com.wowraid.jobspoon.user_dashboard.entity.AttendanceDay;
import com.wowraid.jobspoon.user_dashboard.repository.AttendanceDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService{

    private final AttendanceDayRepository attendanceDayRepository;
    private final AccountRepository accountRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 로그인 성공 시 호출 : 오늘 로그인했는가? 하루 1회만 기록 **/
    @Override
    @Transactional
    public boolean markLogin(Long accountId) {
        LocalDate todayKst = LocalDate.now(KST);

        int rows = attendanceDayRepository.insertIgnore(accountId, todayKst);
        return rows > 0; // 1이면 새로 찍힘, 0이면 이미 있었음

//        Account accountRef = accountRepository.getReferenceById(accountId);
//        AttendanceDay row = AttendanceDay.builder()
//                .account(accountRef)
//                .loginDate(todayKst)
//                .build();
//
//        try{
//            attendanceDayRepository.saveAndFlush(row); // insert 시도
//            return true; // 오늘 처음 출석
//        } catch (DataIntegrityViolationException e) {
//            return false;
//        } catch (Exception e) {
//            throw e; // 예기치 못한 오류는 그대로 터뜨리기
//        }
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

        double rate = (attended * 100.0) / total;

        return new AttendanceRateResponse(rate, attended, total);
    }
}
