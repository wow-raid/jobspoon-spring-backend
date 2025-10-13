package com.wowraid.jobspoon.quiz.controller.request_form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wowraid.jobspoon.quiz.entity.enums.SeedMode;
import com.wowraid.jobspoon.quiz.service.request.CreateQuizSetByFolderRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 단어장 폴더 기반으로 바로 '세션'을 만들기 위한 폼 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateQuizSessionFromFolderRequestForm {

    @JsonProperty("folderId")   private Long folderId;
    @JsonProperty("count")      private int count;
    @JsonProperty("questionType") private String questionType;
    @JsonProperty("difficulty") private String difficulty;
    @JsonProperty("seedMode")   private String seedMode;
    @JsonProperty("fixedSeed")  private Long fixedSeed;
    @JsonProperty("title")      private String title;

    /** 서비스 요청 DTO로 변환(accountId는 컨트롤러에서 주입) */
    public CreateQuizSetByFolderRequest toFolderBasedRequest(Long accountId) {
        CreateQuizSetByFolderRequest.QuestionType qt = parseQuestionType(questionType);
        return new CreateQuizSetByFolderRequest(
                accountId,
                folderId,
                count,
                /* isRandom */ true,
                qt,
                difficulty,
                (title == null || title.isBlank()) ? null : title.trim()
        );
    }

    /** 문자열 questionType -> 서비스 DTO의 enum 매핑. 유효하지 않으면 MIX 기본값 */
    private CreateQuizSetByFolderRequest.QuestionType parseQuestionType(String raw) {
        if (raw == null) return CreateQuizSetByFolderRequest.QuestionType.MIX;
        String key = raw.trim().toUpperCase();
        try {
            return CreateQuizSetByFolderRequest.QuestionType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return CreateQuizSetByFolderRequest.QuestionType.MIX;
        }
    }
}
