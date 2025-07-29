package com.wowraid.jobspoon.term.service.response;

import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CreateTermResponse {

    private final String message;
    private final Long termId;
    private final String title;
    private final String description;
    private final List<String> tags;

    public static CreateTermResponse from(Term term, List<String> tagNames, Category category) {
        String message = "용어가 성공적으로 등록되었습니다.";
        return new CreateTermResponse(
                message,
                term.getId(),
                term.getTitle(),
                term.getDescription(),
                tagNames
        );
    }
}
