package com.wowraid.jobspoon.user_term.controller.request_form;

import com.wowraid.jobspoon.user_term.entity.MemorizationStatus;
import com.wowraid.jobspoon.user_term.service.request.UpdateMemorizationRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemorizationRequestForm {

    @NotNull(message = "status는 반드시 필요합니다.")
    private MemorizationStatus status;

    public UpdateMemorizationRequest toUpdateMemorizationRequest(Long accountId, Long termId) {
        return new UpdateMemorizationRequest(accountId, termId, status);
    }

}
