package com.wowraid.jobspoon.user_trustscore.controller.response;

import com.wowraid.jobspoon.user_trustscore.entity.TrustScoreHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrustScoreHistoryResponse {
    private double score;
    private LocalDateTime recordedAt;

    public static TrustScoreHistoryResponse fromEntity(TrustScoreHistory entity) {
        return TrustScoreHistoryResponse.builder()
                .score(entity.getScore())
                .recordedAt(entity.getRecordedAt())
                .build();
    }
}
