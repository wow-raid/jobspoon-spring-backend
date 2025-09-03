package com.wowraid.jobspoon.quiz.service.request;

import com.wowraid.jobspoon.quiz.entity.QuizSet;
import com.wowraid.jobspoon.term.entity.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class CreateQuizSetByCategoryRequest {

    private final String title;
    private final Long categoryId;
    private final boolean isRandom;

}
