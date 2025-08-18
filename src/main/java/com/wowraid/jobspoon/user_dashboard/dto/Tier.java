package com.wowraid.jobspoon.user_dashboard.dto;

public enum Tier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM;

    public static Tier of(int trustScore) {
        if(trustScore >= 300) return PLATINUM;
        if(trustScore >= 200) return GOLD;
        if(trustScore >= 100) return SILVER;
        return BRONZE;
    }
}
