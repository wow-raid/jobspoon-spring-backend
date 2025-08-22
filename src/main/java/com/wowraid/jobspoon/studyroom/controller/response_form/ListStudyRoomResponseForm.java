package com.wowraid.jobspoon.studyroom.controller.response_form;

import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ListStudyRoomResponseForm {
    private final List<Map<String, Object>> studyRoomList;
    private final boolean hasNext;

    public static ListStudyRoomResponseForm from(ListStudyRoomResponse serviceResponse){
        return new ListStudyRoomResponseForm(
                serviceResponse.getStudyRoomList(),
                serviceResponse.isHasNext()
        );
    }
}
