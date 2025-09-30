package com.wowraid.jobspoon.interviewee_profile.entity;


import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class IntervieweeProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;       // ex) "당근마켓"
    private String major;         // ex) "전공자"
    private String career;        // ex) "3년 이하"
    private String projectExp;    // ex) "있음"
    private String job;           // ex) "Backend"
    private String projectDescription;  // ex) "job-spoon 프로젝트는 ai 면접...."

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "profile_tech_stack", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "tech_stack")
    private List<TechStack> techStack;

    public IntervieweeProfile(String company, String major, String career, String projectExp, String job, String projectDescription, List<TechStack> techStack) {

        this.company = company;
        this.major = major;
        this.career = career;
        this.projectExp = projectExp;
        this.job = job;
        this.projectDescription = projectDescription;
        this.techStack = techStack;
    }

    public IntervieweeProfile() {
    }
}
