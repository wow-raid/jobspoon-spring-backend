package com.wowraid.jobspoon.studyroom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyRoom {

    // 직무별로 필터링을 할 수 있어야함
    //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studyTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int maxMembers;
    private String status;
    private String region;
    private String chatLink;
    private LocalDateTime createdAt;

    public StudyRoom(String studyTitle, String description, int maxMembers, String status, String region, String chatLink) {
        this.studyTitle = studyTitle;
        this.description = description;
        this.maxMembers = maxMembers;
        this.status = status;
        this.region = region;
        this.chatLink = chatLink;
        this.createdAt = LocalDateTime.now();
    }

    public static StudyRoom create(String studyTitle, String description, int maxMembers, String status, String region, String chatLink) {
        return new StudyRoom(studyTitle, description, maxMembers, status, region, chatLink);
    }
}
