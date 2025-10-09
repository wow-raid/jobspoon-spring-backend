package com.wowraid.jobspoon.userTrustscore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trust_score")
public class TrustScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long accountId;

    @Column(nullable = false, name = "attendance_rate")
    private double attendanceRate;   // 이번 달 출석률

    @Column(nullable = false, name = "monthly_interviews")
    private int monthlyInterviews;   // 이번 달 인터뷰 완료 수

    @Column(nullable = false, name = "monthly_problems")
    private int monthlyProblems;     // 이번 달 문제 풀이 수

    @Column(nullable = false, name = "monthly_studyrooms")
    private int monthlyStudyrooms;   // 이번 달 스터디룸 생성 수

    @Column(nullable = false, name = "monthly_comments")
    private int monthlyComments;     // 이번 달 댓글 작성 수

    @Column(nullable = false, name = "monthly_posts")
    private int monthlyPosts;        // 이번 달 게시글 작성 수

    @Column(nullable = false)
    private double score;            // 종합 신뢰 점수

    @Column(nullable = false, name = "calculated_at")
    private LocalDateTime calculatedAt;

    public void update(double attendanceRate, int monthlyInterviews, int monthlyProblems,
                       int monthlyStudyrooms, int monthlyComments, int monthlyPosts, double score) {
        this.attendanceRate = attendanceRate;
        this.monthlyInterviews = monthlyInterviews;
        this.monthlyProblems = monthlyProblems;
        this.monthlyStudyrooms = monthlyStudyrooms;
        this.monthlyComments = monthlyComments;
        this.monthlyPosts = monthlyPosts;
        this.score = score;
        this.calculatedAt = LocalDateTime.now();
    }
}

