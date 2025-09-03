package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor

public class CreateStudyRoomResponseForm {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final String status;
    private final String location;
    private final String studyLevel;
    private final Set<String> recruitingRoles; // 👈 List -> Set
    private final Set<String> skillStack;      // 👈 List -> Set
    private final LocalDateTime createdAt;

    public static CreateStudyRoomResponseForm from(CreateStudyRoomResponse response) {
        return new CreateStudyRoomResponseForm(
                response.getId(),
                response.getTitle(),
                response.getDescription(),
                response.getMaxMembers(),
                response.getStatus(),
                response.getLocation(),
                response.getStudyLevel(),
                response.getRecruitingRoles(),
                response.getSkillStack(),
                response.getCreatedAt()
        );
    }
}