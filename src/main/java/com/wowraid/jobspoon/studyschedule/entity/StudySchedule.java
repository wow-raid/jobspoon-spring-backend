package com.wowraid.jobspoon.studyschedule.entity;

import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)       // 혹시 몰라서 장소 설정까지 네이버지도 api 이용
    private String place;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id",  nullable = false)
    private StudyRoom studyRoom;

    public static StudySchedule create(String title, String content, String place, LocalDateTime startTime, LocalDateTime endTime, StudyRoom studyRoom) {
        StudySchedule schedule = new StudySchedule();
        schedule.title = title;
        schedule.content = content;
        schedule.place = place;
        schedule.startTime = startTime;
        schedule.endTime = endTime;
        schedule.studyRoom = studyRoom;
        return schedule;
    }
}