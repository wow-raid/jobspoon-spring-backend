package com.wowraid.jobspoon.interview.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.account.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.query.sqm.IntervalType;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private boolean isFinished;

    // issue
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_qa_id", nullable = false)
    private InterviewQA interviewQA;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "interviewee_profile_id")
    private IntervieweeProfile intervieweeProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type")
    private InterviewType interviewType;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime deletedAt;


    public Interview(Account account, InterviewQA interviewQA, IntervieweeProfile intervieweeProfile, InterviewType interviewType) {
        this.account = account;
        this.interviewQA = interviewQA;
        this.intervieweeProfile = intervieweeProfile;
        this.createdAt = LocalDateTime.now();
        this.isFinished = false;
        this.interviewType = interviewType;
    }


    public Interview() {

    }
}
