package com.wowraid.jobspoon.account.entity;

public enum RoleType {
    ADMIN("ADMIN"),
    NORMAL("NORMAL");

    private final String label;

    RoleType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
