package com.wowraid.jobspoon.quiz.service.response;

import com.wowraid.jobspoon.quiz.entity.UserQuizAnswer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SubmitAnswerResponse {
    private final Long questionId;
    private final Long selectedChoiceId;
    private final Long userAnswerId;
    private final boolean isCorrect;
    private final LocalDateTime submittedAt;

    public static SubmitAnswerResponse from(UserQuizAnswer answer) {
        return new SubmitAnswerResponse(
                answer.getQuestion().getId(),
                answer.getQuizChoice().getId(),
                answer.getId(),
                answer.isCorrect(),
                answer.getSubmittedAt()
        );
    }
}
