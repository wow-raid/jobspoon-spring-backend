package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyroom.entity.StudyMember;
import com.wowraid.jobspoon.studyroom.repository.StudyMemberRepository;
import com.wowraid.jobspoon.studyschedule.entity.ScheduleAttendance;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import com.wowraid.jobspoon.studyschedule.repository.ScheduleAttendanceRepository;
import com.wowraid.jobspoon.studyschedule.repository.StudyScheduleRepository;
import com.wowraid.jobspoon.studyschedule.service.response.CreateScheduleAttendanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleAttendanceServiceImpl implements ScheduleAttendanceService {

    private final ScheduleAttendanceRepository scheduleAttendanceRepository;
    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Override
    public CreateScheduleAttendanceResponse checkAttendance(Long studyScheduleId, Long accountProfileId) {
        // 1. 요청받은 일정(Schedule) 정보 조회
        StudySchedule schedule = studyScheduleRepository.findById(studyScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        // 2. 해당 일정의 스터디룸 ID와 요청한 사용자의 ID로 스터디 멤버 정보 조회
        Long studyRoomId = schedule.getStudyRoom().getId();
        StudyMember member = studyMemberRepository.findByStudyRoomIdAndAccountProfileId(studyRoomId, accountProfileId)
                .orElseThrow(() -> new IllegalStateException("해당 스터디의 멤버가 아닙니다."));

        // 3. 이미 출석 정보가 있는지 확인 (중복 생성 방지)
        return scheduleAttendanceRepository.findByStudyScheduleIdAndStudyMemberId(studyScheduleId, member.getId())
                .map(CreateScheduleAttendanceResponse::from) // 이미 있다면, 기존 정보를 그대로 반환
                .orElseGet(() -> { // 없다면, 새로 생성 후 반환
                    ScheduleAttendance newAttendance = ScheduleAttendance.create(schedule, member);
                    ScheduleAttendance savedAttendance = scheduleAttendanceRepository.save(newAttendance);
                    return CreateScheduleAttendanceResponse.from(savedAttendance);
                });
    }
}
