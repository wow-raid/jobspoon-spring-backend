package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateStudyRoomResponseForm {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final String status;
    private final String location;
    private final List<String> recruitingRoles;
    private final List<String> skillStack;
    private final LocalDateTime createdAt;

    public static UpdateStudyRoomResponseForm from(UpdateStudyRoomResponse response) {
        return new UpdateStudyRoomResponseForm(
                response.getId(),
                response.getTitle(),
                response.getDescription(),
                response.getMaxMembers(),
                response.getStatus(),
                response.getLocation(),
                response.getRecruitingRoles(),
                response.getSkillStack(),
                response.getCreatedAt()
        );
    }
}
