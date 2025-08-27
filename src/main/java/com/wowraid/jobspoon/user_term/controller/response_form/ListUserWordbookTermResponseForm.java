package com.wowraid.jobspoon.user_term.controller.response_form;

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

    public static ListUserWordbookTermResponseForm from(final ListUserWordbookTermResponse response) {
        List<Map<String, Object>> combinedUserWordbookTermList = response.transformResponseForm();
        return new ListUserWordbookTermResponseForm(
                combinedUserWordbookTermList,
                response.getTotalItems(),
                response.getTotalPages()
        );
    }

}
