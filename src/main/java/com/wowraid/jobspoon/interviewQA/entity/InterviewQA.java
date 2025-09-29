package com.wowraid.jobspoon.interviewQA.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "interview_QA")
public class InterviewQA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Lob
    @Column(name = "question",  nullable = false)
    private String question;

    @Lob
    @Column(name = "answer",   nullable = false)
    private String answer;

    public InterviewQA(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public InterviewQA() {
    }
}
