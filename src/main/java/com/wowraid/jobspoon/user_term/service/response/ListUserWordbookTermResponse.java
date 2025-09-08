package com.wowraid.jobspoon.user_term.service.response;

import com.wowraid.jobspoon.user_term.entity.UserWordbookTerm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ListUserWordbookTermResponse {

    private final List<UserWordbookTerm> userWordbookTermList;
    private final Long totalItems;
    private final Integer totalPages;

    public List<Map<String, Object>> transformResponseForm() {
        return userWordbookTermList.stream()
                .map(userWordbookTerm ->{
                    Map<String, Object> userTermMap = new HashMap<String, Object>();
                    userTermMap.put("id", userWordbookTerm.getId());
                    userTermMap.put("word", userWordbookTerm.getTerm().getTitle());
                    userTermMap.put("description", userWordbookTerm.getTerm().getDescription());
                    return userTermMap;
                })
                .collect(Collectors.toList());
    }

    public static ListUserWordbookTermResponse from(final Page<UserWordbookTerm> paginatedUserWordbookTerm) {
        return new ListUserWordbookTermResponse(
                paginatedUserWordbookTerm.getContent(),
                paginatedUserWordbookTerm.getTotalElements(),
                paginatedUserWordbookTerm.getTotalPages()
        );
    }

}
