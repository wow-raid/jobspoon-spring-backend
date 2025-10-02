package com.wowraid.jobspoon.quiz.service.generator;

import com.wowraid.jobspoon.quiz.entity.enums.DifficultyLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DifficultyProfileResolver {

    private final DifficultyProperties props;

    public DifficultyProperties.Profile resolve(DifficultyLevel level) {
        return props.getProfileOrDefault(level == null ? null : level.name());
    }

    public DifficultyProperties.Profile resolve(String difficulty) {
        return props.getProfileOrDefault(difficulty);
    }
}
