package com.wowraid.jobspoon.user_dashboard.dto;

public record ActivityResponse (
        int attendanceDays,        // 출석일
        long questionTried,        // 문제 시도
        long questionSolved,       // 정답 수
        long posts,                // 게시글 수
        long comments,             // 댓글 수
        int trustScore,            // 신뢰 점수 (메타)
        String tier                // 등급 문자열 (메타)
){}