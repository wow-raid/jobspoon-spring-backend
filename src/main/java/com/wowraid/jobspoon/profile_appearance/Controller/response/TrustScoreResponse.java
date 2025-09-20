package com.wowraid.jobspoon.profile_appearance.Controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TrustScoreResponse {
    private Long accountId;

    private double attendanceRate;   // 이번 달 출석률 (%)
    private int monthlyInterviews;   // 이번 달 인터뷰 완료 횟수
    private int monthlyProblems;     // 이번 달 문제풀이 횟수
    private int monthlyPosts;        // 이번 달 게시글 작성 수
    private int monthlyStudyrooms;   // 이번 달 스터디룸 개설 수
    private int monthlyComments;     // 이번 달 댓글 수

    private double totalScore;       // 최종 점수
    private LocalDateTime calculatedAt;

    // === 변환 메서드 ===
    public static TrustScoreResponse fromEntity(com.wowraid.jobspoon.profile_appearance.Entity.TrustScore entity) {
        return TrustScoreResponse.builder()
                .accountId(entity.getAccountId())
                .attendanceRate(entity.getAttendanceRate())
                .monthlyInterviews(entity.getMonthlyInterviews())
                .monthlyProblems(entity.getMonthlyProblems())
                .monthlyPosts(entity.getMonthlyPosts())
                .monthlyStudyrooms(entity.getMonthlyStudyrooms())
                .monthlyComments(entity.getMonthlyComments())
                .totalScore(entity.getScore())
                .calculatedAt(entity.getCalculatedAt())
                .build();
    }
}
