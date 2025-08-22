package com.wowraid.jobspoon.studyroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.quiz.service.QuizQuestionService;
import com.wowraid.jobspoon.quiz.service.QuizSetService;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.service.StudyRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                "API 테스트 제목", "설명", 5, "온라인",
                List.of("프론트엔드"), List.of("React")
        );

        Account fakeHost = new Account();

        StudyRoom fakeStudyRoom = StudyRoom.create(
                fakeHost,
                "API 테스트 제목",
                "설명",
                5,
                StudyLocation.온라인,
                List.of("프론트엔드"),
                List.of("React")
        );

        // ✅ 테스트가 성공하도록 fakeStudyRoom 객체에 ID 값을 설정합니다.
        ReflectionTestUtils.setField(fakeStudyRoom, "id", 1L);

        // Service의 createStudyRoom 메서드가 호출되면, ID가 설정된 fakeStudyRoom 객체를 반환하도록 설정합니다.
        when(studyRoomService.createStudyRoom(any(CreateStudyRoomRequestForm.class)))
                .thenReturn(fakeStudyRoom);

        // when & then
        mockMvc.perform(post("/api/study-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestForm)))
                .andExpect(status().isCreated())
                // Header의 Location 값을 검증합니다.
                .andExpect(header().string("Location", "/api/study-rooms/1"));
    }
}