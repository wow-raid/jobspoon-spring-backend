package com.wowraid.jobspoon.user_dashboard.dto;

public record ActivityResponse (
        int attendanceDays,   // 출석일
        long questionTried,   // 문제 시도 수
        long questionSolved,  // 정답 처리 수
        long posts,           // 게시글 수
        long comments,        // 댓글 수
        int trustScore,       // 신뢰 점수
        Tier tier           // 등급 문자열
) {}