package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.user_dashboard.entity.AttendanceDay;
import com.wowraid.jobspoon.user_dashboard.repository.AttendanceDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
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
    public void markLogin(Long accountId) {
        LocalDate todayKst = LocalDate.now(KST);

        // 이미 있으면 끝!
        if (attendanceDayRepository.findByAccount_IdAndLoginDate(accountId, todayKst).isPresent())
            return;

        // 없으면 추가
        Account accountRef = accountRepository.findById(accountId);
        AttendanceDay row = AttendanceDay.builder()
                .account(accountRef)
                .loginDate(todayKst)
                .build();

        try{
            attendanceDayRepository.save(row);
            attendanceDayRepository.flush();
        } catch (DataIntegrityViolationException e){
            // 거의 동시에 또 들어와 UNIQUE 충돌 → 이미 누가 선점 저장했으니 조용히 무시
        }
    }

    /** 이번 달 출석률(%) - 달력 기준 **/
    @Override
    @Transactional(readOnly = true)
    public double getThisMonthRate(Long accountId) {

        YearMonth yearMonth = YearMonth.now(KST);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        int attended = attendanceDayRepository.countDaysInMonth(accountId, startDate, endDate);
        int total = yearMonth.lengthOfMonth();

        return (attended * 100.0) / total;
    }
}
