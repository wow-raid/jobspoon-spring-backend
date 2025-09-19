package com.wowraid.jobspoon.user_dashboard.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InterviewResultResponse {
    private Long id;
    private Long interviewId;
    private String summary;       // 요약
    private String qaList;        // Q/A JSON 문자열
    private String hexagonScore;  // 헥사곤 점수 JSON
    private LocalDateTime createdAt;

    public static InterviewResultResponse fromEntity(com.wowraid.jobspoon.user_dashboard.entity.InterviewResult entity) {
        return InterviewResultResponse.builder()
                .id(entity.getId())
                .interviewId(entity.getInterviewId())
                .summary(entity.getSummary())
                .qaList(entity.getQaList())
                .hexagonScore(entity.getHexagonScore())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
