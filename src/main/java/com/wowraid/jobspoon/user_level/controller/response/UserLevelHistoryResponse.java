package com.wowraid.jobspoon.user_level.controller.response;

import com.wowraid.jobspoon.user_level.entity.UserLevelHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLevelHistoryResponse {
    private int level;
    private LocalDateTime achievedAt;

    public static UserLevelHistoryResponse fromEntity(UserLevelHistory entity) {
        return UserLevelHistoryResponse.builder()
                .level(entity.getLevel())
                .achievedAt(entity.getAchievedAt())
                .build();
    }
}
