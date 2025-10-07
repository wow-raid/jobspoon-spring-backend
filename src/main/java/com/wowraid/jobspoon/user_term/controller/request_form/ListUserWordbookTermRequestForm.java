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

    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 20;

    @Min(1)
    private Integer perPage;

    private String sort = "createdAt,desc";

    private int resolvePageSize() {
        if (size != null && size >= 1) return size;
        if (perPage != null && perPage >= 1) return perPage;
        return 20;
    }

    public ListUserWordbookTermRequest toListUserTermRequest(Long accountId, Long folderId) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safePer = resolvePageSize();
        String safeSort = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort.trim();
        return new ListUserWordbookTermRequest(accountId, folderId, safePage, safePer, safeSort);
    }
}
