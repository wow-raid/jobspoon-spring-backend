package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.entity.StudyRole;
import com.wowraid.jobspoon.studyroom.service.response.StudyMemberResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyMemberResponseForm {
    private final Long id;
    private final String nickname;
    private final StudyRole role;

    public static StudyMemberResponseForm from(StudyMemberResponse response) {
        return new StudyMemberResponseForm(
                response.getAccountProfileId(),
                response.getNickname(),
                response.getRole()
        );
    }
}