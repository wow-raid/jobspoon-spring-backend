package com.wowraid.jobspoon.profile_appearance.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TitleCode {
    EARLY_BIRD("EARLY_BIRD", "얼리버드"),
    MASTER("MASTER", "마스터"),
    LEGEND("LEGEND", "레전드");

    private final String code;
    private final String displayName;
}
