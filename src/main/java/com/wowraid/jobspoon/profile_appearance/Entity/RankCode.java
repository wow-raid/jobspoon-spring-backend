package com.wowraid.jobspoon.profile_appearance.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RankCode {
    BRONZE("BRONZE", "브론즈"),
    SILVER("SILVER", "실버"),
    GOLD("GOLD", "골드"),
    PLATINUM("PLATINUM", "플래티넘");

    private final String code;
    private final String displayName;
}
