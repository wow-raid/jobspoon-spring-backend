package com.wowraid.jobspoon.term.service.request;

import com.wowraid.jobspoon.term.entity.Category;
import com.wowraid.jobspoon.term.entity.Term;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UpdateTermRequest {
    private final Long termId;
    private final String title;
    private final String description;
    private final String tags;         // ex: "#HTML #DOM"
    private final String categoryId;

    public Term toUpdateTerm(Category category) {
        return new Term(
                title,
                description,
                category
        );
    }

}
