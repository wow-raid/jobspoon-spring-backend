package com.wowraid.jobspoon.quiz.controller.request_form;

import com.wowraid.jobspoon.quiz.entity.enums.QuestionType;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSessionRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateQuizSessionRequestForm {
    @NotEmpty
    private final List<QuestionType> questionTypes; // CHOICE/OX/INITIALS 최소 1개

    // count 또는 each(아래 3개) 중 하나 방식만 사용
    @Min(1) private final Integer count;
    @Min(0) private final Integer mcqEach;
    @Min(0) private final Integer oxEach;
    @Min(0) private final Integer initialsEach;

    // AUTO | DAILY | FIXED
    private final SeedMode seedMode;      // null이면 AUTO 처리
    private final Long fixedSeed;       // FIXED일 때만 사용

    // EASY | MEDIUM | HARD
    private final String difficulty;    // null이면 MEDIUM 처리
    private final Long folderId;        // null이면 전체 즐겨찾기

    public CreateQuizSessionRequest toServiceRequest(Long accountId) {
        return CreateQuizSessionRequest.builder()
                .accountId(accountId)
                .questionTypes(questionTypes)
                .count(count)
                .mcqEach(mcqEach)
                .oxEach(oxEach)
                .initialsEach(initialsEach)
                .seedMode(seedMode)
                .fixedSeed(fixedSeed)
                .difficulty(difficulty)
                .folderId(folderId)
                .build();
    }
}