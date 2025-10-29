package com.wowraid.jobspoon.quiz.entity.enums;

public enum QuizPartType {
    DAILY_CHOICE, DAILY_OX, DAILY_INITIALS;

    public static QuizPartType fromParam(String raw) {
        if (raw == null) throw new IllegalArgumentException("part is required");
        String s = raw.trim().toUpperCase();
        switch (s) {
            case "CHOICE":
            case "DAILY_CHOICE":
                return DAILY_CHOICE;

            case "OX":
            case "TRUE_FALSE":
            case "TF":
            case "DAILY_OX":
                return DAILY_OX;

            case "INITIALS":
            case "INITIAL":
            case "초성":
            case "DAILY_INITIALS":
                return DAILY_INITIALS;

            default:
                throw new IllegalArgumentException("Unknown part: " + raw);
        }
    }

    public String toShort() {
        switch (this) {
            case DAILY_CHOICE: return "CHOICE";
            case DAILY_OX: return "OX";
            case DAILY_INITIALS: return "INITIALS";
            default: return name();
        }
    }
}