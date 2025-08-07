package com.wowraid.jobspoon.account.entity.enums;

public enum LoginType {
    KAKAO("Kakao"),
    GOOGLE("google"),
    NAVER("naver"),
    GITHUB("github"),
    GUEST("guest");

    private final String label;

    LoginType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
