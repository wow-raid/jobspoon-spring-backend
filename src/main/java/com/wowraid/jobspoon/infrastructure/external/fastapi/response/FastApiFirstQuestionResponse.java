package com.wowraid.jobspoon.infrastructure.external.fastapi.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class FastApiFirstQuestionResponse {

    private Long interviewId;
    private List<String> questions;
    private List<Long> questionIds;

}
