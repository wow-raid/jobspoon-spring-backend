package com.wowraid.jobspoon.user_term.controller.response_form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wowraid.jobspoon.user_term.service.response.ListUserWordbookTermResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ListUserWordbookTermResponseForm {

    private final List<Map<String, Object>> userWordbookTermList;
    private final Long totalItems;
    private final Integer totalPages;

    private final Integer page;
    private final Integer size;
    private final String sort;

    @JsonProperty("items")
    public List<Map<String, Object>> getItemsAlias() {
        return userWordbookTermList;
    }

    @JsonProperty("total")
    public Long getTotalAlias(){
        return totalItems;
    }

    public static ListUserWordbookTermResponseForm from(
            final ListUserWordbookTermResponse response,
            int page, int size, String sort
    ) {
        List<Map<String, Object>> combined = response.transformResponseForm();
        return new ListUserWordbookTermResponseForm(
                combined,
                response.getTotalItems(),
                response.getTotalPages(),
                page,
                size,
                sort
        );
    }


    public static ListUserWordbookTermResponseForm from(final ListUserWordbookTermResponse response) {
        List<Map<String, Object>> combinedUserWordbookTermList = response.transformResponseForm();
        return new ListUserWordbookTermResponseForm(
                combinedUserWordbookTermList,
                response.getTotalItems(),
                response.getTotalPages(),
                null, null, null
        );
    }

}
