package com.wowraid.jobspoon.profile_appearance.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TitleCode {
    EARLY_BIRD("EARLY_BIRD", "얼리버드"),
    BEGINNER("BEGINNER", "초심자"),
    EXPERT("EXPERT", "전문가"),
    LEGEND("LEGEND", "레전드");

    private final String code;
    private final String displayName;
}
