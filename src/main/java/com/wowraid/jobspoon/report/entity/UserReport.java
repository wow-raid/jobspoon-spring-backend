package com.wowraid.jobspoon.report.entity;

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
public class UserReport {

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
    private ReportCategory category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자
    private UserReport(AccountProfile reporter, AccountProfile reportedUser, StudyRoom studyRoom, ReportCategory category, String description) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.studyRoom = studyRoom;
        this.category = category;
        this.description = description;
        this.status = ReportStatus.PENDING; // 신고 생성 시 기본 상태는 PENDING
    }

    // 정적 팩토리 메소드
    public static UserReport create(AccountProfile reporter, AccountProfile reportedUser, StudyRoom studyRoom, ReportCategory category, String description) {
        return new UserReport(reporter, reportedUser, studyRoom, category, description);
    }
}