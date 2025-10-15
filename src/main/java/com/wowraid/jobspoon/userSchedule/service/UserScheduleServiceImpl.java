package com.wowraid.jobspoon.userSchedule.service;

import com.wowraid.jobspoon.userSchedule.controller.request.UserScheduleRequest;
import com.wowraid.jobspoon.userSchedule.entity.UserSchedule;
import com.wowraid.jobspoon.userSchedule.repository.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserScheduleServiceImpl implements UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;

    // 일정 등록
    @Override
    public UserSchedule createUserSchedule(Long accountId, UserScheduleRequest request) {

        // 제목 필수
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        LocalDateTime start;
        LocalDateTime end;

        // allDay = true일 경우 날짜 기반으로 00:00~23:59 보정
        if (request.isAllDay()) {
            LocalDate baseDate;

            // 프론트에서 startTime이 null일 수도 있으므로 안전하게 처리
            if (request.getStartTime() != null) {
                baseDate = request.getStartTime().toLocalDate();
            } else {
                baseDate = LocalDate.now(); // fallback: 오늘 날짜
            }

            start = baseDate.atStartOfDay();                // 00:00
            end = baseDate.atTime(23, 59, 59);              // 23:59
        }
        // 일반 일정은 그대로 저장
        else {
            if (request.getStartTime() == null || request.getEndTime() == null) {
                throw new IllegalArgumentException("StartTime and EndTime are required for non-allDay events");
            }
            start = request.getStartTime();
            end = request.getEndTime();
        }

        // 엔티티 생성
        UserSchedule schedule = UserSchedule.builder()
                .accountId(accountId)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(start)
                .endTime(end)
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

    // 일정 수정
    @Override
    public UserSchedule updateUserSchedule(Long accountId, Long id, UserScheduleRequest request){
        UserSchedule schedule = userScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

        // 본인 일정만 수정 가능
        if (!schedule.getAccountId().equals(accountId)) {
            throw new IllegalStateException("본인 일정만 수정할 수 있습니다.");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            schedule.setTitle(request.getTitle());
        }

        if(request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }

        // 수정 시에도 allDay에 따라 보정
        if (request.isAllDay()) {
            LocalDate baseDate = schedule.getStartTime() != null
                    ? schedule.getStartTime().toLocalDate()
                    : LocalDate.now();
            schedule.setStartTime(baseDate.atStartOfDay());
            schedule.setEndTime(baseDate.atTime(23, 59, 59));
        } else {
            if (request.getStartTime() != null) schedule.setStartTime(request.getStartTime());
            if (request.getEndTime() != null) schedule.setEndTime(request.getEndTime());
        }

        if(request.getLocation() != null) {
            schedule.setLocation(request.getLocation());
        }

        if(request.getColor() != null) {
            schedule.setColor(request.getColor());
        }

        schedule.setAllDay(request.isAllDay());

        return schedule;
    }
}
