package com.wowraid.jobspoon.quiz.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum QuestionType {
    CHOICE,
    OX;

    @JsonCreator
    public static QuestionType from(String value){
        return QuestionType.valueOf(value.toUpperCase());
    }
}
