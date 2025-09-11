package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.controller.response_form.InterviewCompletionResponse;
import com.wowraid.jobspoon.user_dashboard.entity.InterviewStatus;
import com.wowraid.jobspoon.user_dashboard.repository.InterviewSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class InterviewSummaryServiceImpl implements InterviewSummaryService {

    private final InterviewSummaryRepository interviewSummaryRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 모의면접 완료 횟수 (누적 + 이번 달) **/
    @Override
    @Transactional(readOnly = true)
    public InterviewCompletionResponse getCompletionStatus(Long accountId){

        // 이번 달(1일 ~ 말일)
        YearMonth yearMonth = YearMonth.now(KST);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay(KST).toLocalDateTime();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(KST).toLocalDateTime();

        long totalCompleted = interviewSummaryRepository
                .countByAccountIdAndStatus(accountId, InterviewStatus.COMPLETED);

        long monthlyCompleted = interviewSummaryRepository
                .countByAccountIdAndStatusAndCreatedAtBetween(accountId, InterviewStatus.COMPLETED, start, end);

        return new InterviewCompletionResponse(totalCompleted, monthlyCompleted);
    }
}
