package com.wowraid.jobspoon.user_dashboard.entity;

/*
    사용자 활동 지표 저장 테이블
    다른 도메인에서 집계하여 반영하거나
    DB view로 합쳐도 됨
 */

import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //대시보드에서의 컬럼명 : account_id
    //실제 join은 account의 id로 1:1 맵핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", referencedColumnName = "id", nullable=false, unique=true)
    private Account account;

    //출석률(%)
    private double attendanceRate;
    //모의 면접 횟수
    private Long mockInterviewCount;
    //문제 풀이 횟수
    private Long problemSolvingCount;
    //게시글 작성 횟수
    private Long postCount;

    //신뢰 점수(0~100)
    private int trustScore;
    //신뢰 등급(A/B/C/...)
    private String trustGrade;

    //팩토리 메소드
    public static UserDashboard initFor(Account account) {
        UserDashboard dashboard = new UserDashboard();
        dashboard.account = account;
        dashboard.attendanceRate = 0.0;
        dashboard.mockInterviewCount = 0L;
        dashboard.problemSolvingCount = 0L;
        dashboard.postCount = 0L;
        dashboard.trustScore = 0;
        dashboard.trustGrade = "C";
        return dashboard;
    }
}
