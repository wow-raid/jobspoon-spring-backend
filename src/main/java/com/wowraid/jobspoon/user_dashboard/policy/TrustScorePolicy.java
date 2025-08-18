package com.wowraid.jobspoon.user_dashboard.policy;

import org.springframework.stereotype.Component;

@Component
public class TrustScorePolicy {
    // 가중치 (표 기준)
    private static final int ATTENDANCE_POINT = 2;   // 출석 1회 +2
    private static final int SOLVED_NUM = 3;         // 정답 1건 +1.5 -> *3/2
    private static final int SOLVED_DEN = 2;
    private static final int TRIED_POINT = 1;        // 시도 1건 +1
    private static final int POST_POINT = 2;         // 게시글 1건 +2
    private static final int COMMENT_POINT = 1;      // 댓글 1건 +1
//    private static final int LONG_ABSENCE_PENALTY = -5; // 30일 이상 미접속 -5
}
