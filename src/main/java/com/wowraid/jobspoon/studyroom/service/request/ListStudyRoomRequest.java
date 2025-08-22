package com.wowraid.jobspoon.studyroom.service.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ListStudyRoomRequest {
    private final Long lastStudyId;
    private final int size;
}
