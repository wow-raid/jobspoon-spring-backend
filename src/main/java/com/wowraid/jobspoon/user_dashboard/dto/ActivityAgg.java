package com.wowraid.jobspoon.user_dashboard.dto;

// 집계만 담는 DTO (메타 없음)
public record ActivityAgg(
        int attendanceDays,
        long questionTried,
        long questionSolved,
        long posts,
        long comments
) {}