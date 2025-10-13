package com.wowraid.jobspoon.userSchedule.service;

import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;
import com.wowraid.jobspoon.userSchedule.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserScheduleServiceImpl implements UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;

    // 일정 등록
    @Override
    public UserSchedule createUserSchedule(Long accountId, UserScheduleRequest request) {

        // 필수값 검증
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("StartTime and EndTime are required");
        }

        // 엔티티 생성
        UserSchedule schedule = UserSchedule.builder()
                .accountId(accountId)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .allDay(request.isAllDay())
                .color(request.getColor() != null ? request.getColor() : "#3b82f6")
                .build();

        return userScheduleRepository.save(schedule);
    }

    // 전체 일정 조회
    @Override
    public List<UserSchedule> getUserSchedules(Long accountId) {
        return userScheduleRepository.findAllByAccountId(accountId);
    }

    // 특정 일정 상세 조회
    @Override
    public UserSchedule getUserScheduleById(Long accountId, Long id) {
        UserSchedule schedule = userScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

        if (!schedule.getAccountId().equals(accountId)) {
            throw new IllegalStateException("본인 일정만 조회할 수 있습니다.");
        }

        return schedule;
    }

    // 일정 삭제
    @Override
    public void deleteUserSchedule(Long accountId, Long id) {
        UserSchedule schedule = userScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

        if (!schedule.getAccountId().equals(accountId)) {
            throw new IllegalStateException("본인 일정만 삭제할 수 있습니다.");
        }

        userScheduleRepository.delete(schedule);
    }
}
