package com.wowraid.jobspoon.user_dashboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 완료 결과 기록

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="interview_result")
public class InterviewResult {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;  // Interview.id 참조 (FK 대신 단순 매핑)

    @Column(columnDefinition = "text")
    private String summary;  // 요약 (AI가 생성한 텍스트)

    @Column(name = "qa_list", columnDefinition = "json")
    private String qaList;   // Q/A 결과 JSON

    @Column(name = "hexagon_score", columnDefinition = "json")
    private String hexagonScore; // 역량 점수 JSON

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setQaList(String qaList) {
        this.qaList = qaList;
    }

    public void setHexagonScore(String hexagonScore) {
        this.hexagonScore = hexagonScore;
    }
}
