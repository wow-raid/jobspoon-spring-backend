package com.wowraid.jobspoon.interview.service.response;

import com.wowraid.jobspoon.interview.entity.InterviewType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InterviewResultListResponse {

    private boolean isFinished;

    private LocalDateTime createdAt;

    private String sender;

    private InterviewType interviewType;

    private Long interviewId;

}
