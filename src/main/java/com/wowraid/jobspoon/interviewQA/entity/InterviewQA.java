package com.wowraid.jobspoon.interviewQA.entity;

import com.wowraid.jobspoon.interview.entity.Interview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "interview_QA")
public class InterviewQA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Lob
    @Column(name = "question")
    private String question;

    @Lob
    @Column(name = "answer",   nullable = true)
    private String answer;

    public InterviewQA(Interview interview, String question, String answer) {
        this.interview = interview;
        this.question = question;
        this.answer = answer;
    }

    public InterviewQA(Interview interview, String question) {
        this.interview = interview;
        this.question = question;
    }

    public InterviewQA(String question) {
        this.question = question;
    }

    public InterviewQA(Interview interview) {
        this.interview = interview;
    }

    public InterviewQA() {
    }
}
