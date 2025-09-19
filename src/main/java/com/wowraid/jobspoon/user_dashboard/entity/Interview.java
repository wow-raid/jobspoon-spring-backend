package com.wowraid.jobspoon.user_dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 시도 기록

@Entity
@Getter
@NoArgsConstructor
@Table(name="interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(length = 50, nullable = false)
    private String topic;   // 면접 주제

    @Column(name = "experience_level", nullable = false)
    private int experienceLevel;

    @Column(name = "project_experience", nullable = false)
    private int projectExperience;

    @Column(name = "academic_background", nullable = false)
    private int academicBackground;

    @Column(name = "tech_stack", columnDefinition = "json", nullable = false)
    private String techStack;   // JSON 저장 (["Spring", "React"] 같은 문자열)

    @Column(name = "company_name", length = 20, nullable = false)
    private String companyName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // === Lifecycle ===
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Interview(Long accountId, String topic, int experienceLevel,
                     int projectExperience, int academicBackground,
                     String techStack, String companyName) {
        this.accountId = accountId;
        this.topic = topic;
        this.experienceLevel = experienceLevel;
        this.projectExperience = projectExperience;
        this.academicBackground = academicBackground;
        this.techStack = techStack;
        this.companyName = companyName;
    }
}
