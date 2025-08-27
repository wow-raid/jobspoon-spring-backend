package com.wowraid.jobspoon.user_term.controller.request_form;

import com.wowraid.jobspoon.user_term.service.request.ListUserWordbookTermRequest;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ListUserWordbookTermRequestForm {

    @Min(1)
    private Integer page = 1;

    @Min(1)
    private Integer perPage = 10;

    private String sort = "createdAt,desc";

    public ListUserWordbookTermRequest toListUserTermRequest(Long accountId, Long folderId) {
        int safePage = (page == null || page < 1) ? 1 : page;
        int safePer = (perPage == null || perPage < 1) ? 10 : perPage;
        String safeSort = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort.trim();
        return new ListUserWordbookTermRequest(accountId, folderId, safePage, safePer, safeSort);
    }
}
