package com.wowraid.jobspoon.studyroom.entity;

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Account host;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer maxMembers;

    @Enumerated(EnumType.STRING)        // DB에서는 Enum의 이름이 문자열로 저장됨
    @Column(nullable = false)
    private StudyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyLocation location;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recruiting_roles", joinColumns = @JoinColumn(name = "study_room_id"))
    @Column(name = "role_name")
    private List<String> recruitingRoles;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "skill_stack", joinColumns = @JoinColumn(name = "study_room_id"))
    @Column(name = "skill_name")
    private List<String> skillStack;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // 생성자 추가
    private StudyRoom(Account host, String title, String description, Integer maxMembers, StudyLocation location, List<String> recruitingRoles, List<String> skillStack) {
        this.host = host;
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.status = StudyStatus.RECRUITING;
        this.location = location;
        this.recruitingRoles = recruitingRoles;
        this.skillStack = skillStack;
    }

    // create 정적 팩토리 메서드 추가
    public static StudyRoom create(Account host, String title, String description, Integer maxMembers, StudyLocation location, List<String> recruitingRoles, List<String> skillStack) {
        return new StudyRoom(host, title, description, maxMembers, location, recruitingRoles, skillStack);
    }
}