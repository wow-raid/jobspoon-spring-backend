package com.wowraid.jobspoon.quiz.controller.request_form;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartQuizSessionUnifiedRequestForm {

    @NotBlank
    @Pattern(regexp="(?i)folder|category")
    private String source;      // "folder" | "category"

    @JsonAlias({"userFolderId","userWordbookFolderId","folder_id"})
    private Long folderId;      // source=folder 일 때 필수

    @JsonAlias({"category_id"})
    private Long categoryId;    // source=category 일 때 필수

    @Min(1) @Max(100)
    private int count;

    @NotBlank
    @JsonAlias({"questionType"})
    @Pattern(regexp="(?i)mix|choice|ox|initials")
    private String type;        // "mix" | "choice" | "ox" | "initials"

    @NotBlank
    @JsonAlias({"difficulty"})
    @Pattern(regexp="(?i)mix|easy|medium|hard")
    private String level;       // "mix" | "easy" | "medium" | "hard"

    @JsonAlias({"seed_mode"})
    private String seedMode;    // "AUTO" | "DAILY" | "FIXED" (대소문자 무시)

    private Long fixedSeed;     // FIXED일 때 시드값

    public StartQuizSessionByCategoryRequestForm toCategoryForm() {
        StartQuizSessionByCategoryRequestForm f = new StartQuizSessionByCategoryRequestForm();
        f.setCategoryId(this.categoryId);
        f.setCount(this.count);
        f.setQuestionType(this.type);
        f.setDifficulty(this.level);
        f.setSeedMode(this.seedMode);
        f.setFixedSeed(this.fixedSeed);
        return f;
    }

    public CreateQuizSessionFromFolderRequestForm toFolderForm() {
        CreateQuizSessionFromFolderRequestForm f = new CreateQuizSessionFromFolderRequestForm();
        f.setFolderId(this.folderId);
        f.setCount(this.count);
        f.setQuestionType(this.type);
        f.setDifficulty(this.level);
        f.setSeedMode(this.seedMode);
        f.setFixedSeed(this.fixedSeed);
        return f;
    }
}
