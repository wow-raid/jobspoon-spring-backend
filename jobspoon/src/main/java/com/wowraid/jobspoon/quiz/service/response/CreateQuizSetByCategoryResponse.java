package com.wowraid.jobspoon.quiz.service.response;

import com.wowraid.jobspoon.quiz.entity.QuizSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CreateQuizSetByCategoryResponse {
    private final String message;
    private final Long quizSetId;
    private final String title;
    private final boolean isRandom;

    public static CreateQuizSetByCategoryResponse from(QuizSet quizSet) {
        String message = "퀴즈 세트가 성공적으로 등록되었습니다.";
        return new CreateQuizSetByCategoryResponse(
                message,
                quizSet.getId(),
                quizSet.getTitle(),
                quizSet.isRandom()
        );
    }
}
