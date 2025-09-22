package com.wowraid.jobspoon.profile_appearance.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TitleCode {
    // 📌 기존
    EARLY_BIRD("EARLY_BIRD", "얼리버드"),
    BEGINNER("BEGINNER", "초심자"),
    EXPERT("EXPERT", "전문가"),
    LEGEND("LEGEND", "레전드"),

    // 🎓 학습/지식 기반
    ROOKIE("ROOKIE", "루키"),
    LEARNER("LEARNER", "학습자"),
    SCHOLAR("SCHOLAR", "학자"),
    SPECIALIST("SPECIALIST", "전문가"),
    ARCHITECT("ARCHITECT", "아키텍트"),
    GURU("GURU", "구루"),

    // 🏅 시험/랭크 느낌
    CANDIDATE("CANDIDATE", "지원자"),
    CHALLENGER("CHALLENGER", "도전자"),
    ACHIEVER("ACHIEVER", "성취자"),
    ELITE("ELITE", "엘리트"),
    MASTER("MASTER", "마스터"),
    GRANDMASTER("GRANDMASTER", "그랜드마스터"),

    // 🌟 학습 태도/습관 기반
    NIGHT_OWL("NIGHT_OWL", "올빼미"),
    HARD_WORKER("HARD_WORKER", "노력가"),
    MARATHONER("MARATHONER", "마라토너"),
    SPRINTER("SPRINTER", "스프린터");

    private final String code;
    private final String displayName;
}