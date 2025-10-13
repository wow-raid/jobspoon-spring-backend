package com.wowraid.jobspoon.quiz.controller.request_form;

import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByCategoryRequest;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 카테고리 기반으로 바로 '세션'을 만들기 위한 폼 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizSessionByCategoryRequestForm {

    @NotNull
    private Long categoryId;

    // 프런트에서 고르는 값
    @Min(1) @Max(100)
    private int count;

    @NotBlank
    @Pattern(regexp = "(?i)mix|mcq|ox|initial",
            message = "questionType은 MIX/MCQ/OX/INITIAL 중 하나여야 합니다.")
    private String questionType;

    @NotBlank
    @Pattern(regexp = "(?i)mix|easy|normal|hard|medium",
            message = "difficulty는 MIX/EASY/NORMAL/HARD(MEDIUM) 중 하나여야 합니다.")
    private String difficulty;

    // 시드 전략
    @NotBlank
    @Pattern(regexp = "(?i)auto|daily|fixed",
            message = "seedMode는 AUTO/DAILY/FIXED 중 하나여야 합니다.")
    private String seedMode;

    private Long fixedSeed; // seedMode=FIXED 일 때만 사용

    /** 서비스 레이어 요청으로 변환 (count/type/difficulty 포함) */
    public CreateQuizSetByCategoryRequest toCategoryBasedRequest() {
        boolean isRandom = ! "FIXED".equalsIgnoreCase(seedMode);
        return new CreateQuizSetByCategoryRequest(
                /* title */ null,
                categoryId,
                isRandom,
                count,
                CreateQuizSetByCategoryRequest.QuestionType.from(questionType),
                CreateQuizSetByCategoryRequest.DifficultyLevel.from(difficulty)
        );
    }
}
