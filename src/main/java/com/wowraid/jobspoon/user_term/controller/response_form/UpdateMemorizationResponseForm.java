package com.wowraid.jobspoon.user_term.controller.response_form;

import com.wowraid.jobspoon.user_term.controller.request_form.UpdateMemorizationRequestForm;
import com.wowraid.jobspoon.user_term.entity.MemorizationStatus;
import com.wowraid.jobspoon.user_term.service.response.UpdateMemorizationResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateMemorizationResponseForm {
    private final Long termId;
    private final MemorizationStatus status;
    private final LocalDateTime memorizedAt;
    private final boolean changed;

    public static UpdateMemorizationResponseForm from(UpdateMemorizationResponse response) {
        return UpdateMemorizationResponseForm.builder()
                .termId(response.getTermId())
                .status(response.getStatus())
                .memorizedAt(response.getMemorizedAt())
                .changed(response.isChanged())
                .build();
    }
}
