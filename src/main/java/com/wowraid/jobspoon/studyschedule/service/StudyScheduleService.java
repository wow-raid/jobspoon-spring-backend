package com.wowraid.jobspoon.studyschedule.service;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyschedule.controller.request_form.RegisterStudyScheduleRequestForm;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import com.wowraid.jobspoon.studyschedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyScheduleService {
    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyRoomRepository studyRoomRepository;


    // 생성!!
    @Transactional
    public Long createStudySchedule(Long studyRoomId, RegisterStudyScheduleRequestForm request){
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        StudySchedule schedule = StudySchedule.create(
                request.getTitle(),
                request.getContent(),
                request.getPlace(),
                request.getStartTime(),
                request.getEndTime(),
                studyRoom
        );
        return studyScheduleRepository.save(schedule).getId();
    }

    // 스터디룸 내 스케줄 전체조회
    public List<StudySchedule> findScheduleByStudyRoom(Long studyRoomId){
        return studyScheduleRepository.findAllByStudyRoomId(studyRoomId);
    }

    // 스터디룸 내 스케줄 특정조회
    public StudySchedule findScheduleById(Long scheduleId){
        return studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));
    }
}
