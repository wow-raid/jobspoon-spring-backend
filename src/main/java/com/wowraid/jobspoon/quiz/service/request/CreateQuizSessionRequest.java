package com.wowraid.jobspoon.quiz.service.request;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizSessionRequest {
    private Long accountId;
    private List<QuestionType> questionTypes;
    private Integer count;
    private Integer mcqEach;
    private Integer oxEach;
    private Integer initialsEach;
    private String seedMode;
    private String difficulty;
    private Long folderId;
}
