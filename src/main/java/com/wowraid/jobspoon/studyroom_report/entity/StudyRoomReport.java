package com.wowraid.jobspoon.studyroom_report.entity;

import com.wowraid.jobspoon.accountProfile.entity.AccountProfile;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class StudyRoomReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고한 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private AccountProfile reporter;

    // 신고된 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private AccountProfile reportedUser;

    // 신고가 발생한 스터디모임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_room_id", nullable = false)
    private StudyRoom studyRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyRoomReportCategory category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyRoomReportStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    private StudyRoomReport(AccountProfile reporter, AccountProfile reportedUser, StudyRoom studyRoom, StudyRoomReportCategory category, String description) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.studyRoom = studyRoom;
        this.category = category;
        this.description = description;
        this.status = StudyRoomReportStatus.PENDING; // 신고 생성 시 기본 상태는 PENDING
    }

    // 정적 팩토리 메소드
    public static StudyRoomReport create(AccountProfile reporter, AccountProfile reportedUser, StudyRoom studyRoom, StudyRoomReportCategory category, String description) {
        return new StudyRoomReport(reporter, reportedUser, studyRoom, category, description);
    }

    public void updateStatus(StudyRoomReportStatus status) {
        if (status == null) {
            return;
        }
        this.status = status;
    }
}