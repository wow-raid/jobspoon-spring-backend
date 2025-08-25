package com.wowraid.jobspoon.studyroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.controller.request_Form.UpdateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLevel;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import com.wowraid.jobspoon.studyroom.service.response.CreateStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class StudyRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyRoomService studyRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("스터디룸 생성 API 테스트")
    void createStudyRoom() throws Exception {
        // given
        final CreateStudyRoomRequestForm requestForm = new CreateStudyRoomRequestForm(
                "API 테스트 제목", "설명", 5, "SEOUL",
                "NEWBIE", List.of("프론트엔드"), List.of("React")
        );

        // Service가 반환할 가짜 StudyRoom 엔터리를 준비함
        Account fakeHost = new Account(1L);
        StudyRoom fakeStudyRoom = StudyRoom.create(
                fakeHost,
                "API 테스트 제목", "설명", 5, StudyLocation.SEOUL,
                StudyLevel.NEWBIE, List.of("프론트엔드"), List.of("React")
        );
        // 테스트를 위해 ID를 임의로 설정
        ReflectionTestUtils.setField(fakeStudyRoom, "id", 1L);

        // Service가 반환할 가짜 응답 객체 생성
        CreateStudyRoomResponse serviceResponse = new CreateStudyRoomResponse(
                1L,
                "API 테스트 제목",
                "설명",
                Integer.valueOf(5),
                "RECRUITING",
                "SEOUL",
                "NEWBIE",
                List.of("프론트엔드"),
                List.of("React"),
                LocalDateTime.now()
        );
        // Service의 createStudyRoom 메서드가 호출되면, 위에서 만든 fakeStudyRoom 엔티티를 반환하도록 설정합니다.
        when(studyRoomService.createStudyRoom(any(CreateStudyRoomRequestForm.class), anyLong()))
                .thenReturn(fakeStudyRoom);

        // when & then
        mockMvc.perform(post("/api/study-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForm)))
                .andExpect(status().isCreated())
                // Controller가 Location 헤더를 반환하므로, 헤더를 검증합니다.
                .andExpect(header().string("Location", "/api/study-rooms/1"));
    }

    @Test
    @DisplayName("스터디룸 목록 조회 API 테스트")
    void getAllStudyRooms() throws Exception {
        // given
        ListStudyRoomResponse serviceResponse = new ListStudyRoomResponse(
                new SliceImpl<>(Collections.emptyList())
        );

        when(studyRoomService.findAllStudyRooms(any())).thenReturn(serviceResponse);

        // when & then
        mockMvc.perform(get("/api/study-rooms")
                .param("size", "10")
                .param("lastStudyId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.studyRoomList").isArray());
    }

    @Test
    @DisplayName("스터디모임 수정 API 테스트")
    void updateStudyRoom() throws Exception {
        // given
        final Long studyRoomId = 1L;
        final UpdateStudyRoomRequestForm requestForm = new UpdateStudyRoomRequestForm(
                "수정된 API 제목", "수정된 설명", 8, "BUSAN",
                "SENIOR", List.of("풀스텍"), List.of("Kotlin")
        );

        // service가 반환할 가짜 응답 객체 생성
        UpdateStudyRoomResponse serviceResponse = new UpdateStudyRoomResponse(
                studyRoomId, "수정된 API 제목", "수정된 설명", Integer.valueOf(8), "RECRUITING", "BUSAN", "SENIOR",
                List.of("풀스택"), List.of("Kotlin"), LocalDateTime.now()
        );

        // studyRoomService.updateStudyRoom 매서드가 호출되면, 위에서 만든 serviceResponse를 반환하도록 설정
        when(studyRoomService.updateStudyRoom(eq(studyRoomId), any())).thenReturn(serviceResponse);

        // when & then
        mockMvc.perform(put("/api/study-rooms/" + studyRoomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studyRoomId))
                .andExpect(jsonPath("$.title").value("수정된 API 제목"))
                .andExpect(jsonPath("$.location").value("BUSAN"));
    }
}