package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.ReadStudyRoomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReadStudyRoomResponseForm {
    private final Long id;
    private final String title;
    private final String description;
    private final Integer maxMembers;
    private final String status;
    private final String location;
    private final String studyLevel;
    private final List<String> recruitingRoles;
    private final List<String> skillStack;
    private final LocalDateTime createdAt;
    private final Long hostId;

    public static ReadStudyRoomResponseForm from(ReadStudyRoomResponse response) {
        return new ReadStudyRoomResponseForm(
                response.getId(),
                response.getTitle(),
                response.getDescription(),
                response.getMaxMembers(),
                response.getStatus(),
                response.getLocation(),
                response.getStudyLevel(),
                response.getRecruitingRoles(),
                response.getSkillStack(),
                response.getCreatedAt(),
                response.getHostId()
        );
    }
}