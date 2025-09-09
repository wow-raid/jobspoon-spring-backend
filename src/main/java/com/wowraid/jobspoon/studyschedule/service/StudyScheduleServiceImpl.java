package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.accountProfile.repository.AccountProfileRepository;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import com.wowraid.jobspoon.studyschedule.repository.StudyScheduleRepository;
import com.wowraid.jobspoon.studyschedule.service.request.CreateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.request.UpdateStudyScheduleRequest;
import com.wowraid.jobspoon.studyschedule.service.response.CreateStudyScheduleResponse;
import com.wowraid.jobspoon.studyschedule.service.response.ListStudyScheduleResponse;
import com.wowraid.jobspoon.studyschedule.service.response.ReadStudyScheduleResponse;
import com.wowraid.jobspoon.studyschedule.service.response.UpdateStudyScheduleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyScheduleServiceImpl implements StudyScheduleService {
    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final AccountProfileRepository accountProfileRepository;

    @Override
    @Transactional
    public CreateStudyScheduleResponse createSchedule(CreateStudyScheduleRequest request) {
        StudyRoom studyRoom = studyRoomRepository.findById(request.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));
        AccountProfile author = accountProfileRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        StudySchedule schedule = StudySchedule.create(
                studyRoom,
                author,
                request.getTitle(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime()
        );
        StudySchedule savedSchedule = studyScheduleRepository.save(schedule);
        return CreateStudyScheduleResponse.from(savedSchedule);
    }

    @Override
    public List<ListStudyScheduleResponse> findAllSchedules(Long studyRoomId) {
        return studyScheduleRepository.findAllByStudyRoomId(studyRoomId)
                .stream()
                .map(ListStudyScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStudyScheduleResponse findScheduleById(Long scheduleId) {
        StudySchedule schedule = studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        return ReadStudyScheduleResponse.from(schedule);
    }

    @Override
    @Transactional
    public UpdateStudyScheduleResponse updateSchedule(Long scheduleId, Long currentUserId, UpdateStudyScheduleRequest request) {
        StudySchedule schedule = studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        Long authorId = schedule.getAuthor().getId();
        Long leaderId = schedule.getStudyRoom().getHost().getId();

        if (!authorId.equals(currentUserId) && !leaderId.equals(currentUserId)) {
            throw new IllegalStateException("일정을 수정할 권한이 없습니다.");
        }

        schedule.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime()
        );

        return UpdateStudyScheduleResponse.from(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId, Long currentUserId) {
        StudySchedule schedule = studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        Long authorId = schedule.getAuthor().getId();
        Long leaderId = schedule.getStudyRoom().getHost().getId();

        if (!authorId.equals(currentUserId) && !leaderId.equals(currentUserId)) {
            throw new IllegalStateException("일정을 삭제할 권한이 없습니다.");
        }

        studyScheduleRepository.delete(schedule);
    }
}