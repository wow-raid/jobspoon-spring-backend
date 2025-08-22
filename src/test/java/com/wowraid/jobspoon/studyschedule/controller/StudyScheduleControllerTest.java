package com.wowraid.jobspoon.studyschedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyschedule.controller.request_form.CreateStudyScheduleRequestForm;
import com.wowraid.jobspoon.studyschedule.entity.StudySchedule;
import com.wowraid.jobspoon.studyschedule.repository.StudyScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StudyScheduleControllerTest {
    @Autowired
    private MockMvc mockMvc; // API를 테스트하기 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // Java 객체를 JSON으로 변환하기 위한 객체

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private StudyScheduleRepository studyScheduleRepository;

    private StudyRoom savedStudyRoom;

    @BeforeEach
    void setUp() {
        StudyRoom studyRoom = new StudyRoom(
                "스터디룸 테스트합니다.",
                "배고픈데 짜장면 드실분",
                10,
                "모집중",
                "온라인",
                "https://m.openkakao/Roto90");
        savedStudyRoom = studyRoomRepository.save(studyRoom);
    }

    @Test
    @DisplayName("스케줄을 성공적으로 생성했다!")
    void createStudySchedule_Success() throws Exception {
        // given
        CreateStudyScheduleRequestForm request = new CreateStudyScheduleRequestForm(
                "스스메!", "신조 사사게오~", "온라인 (Discord)",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/study-rooms/{studyRoomId}/schedules", savedStudyRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andDo(print());
    }

    @Test
    @DisplayName("참가한 스터디룸의 전체 스케줄을 조회합니동동주!")
    void findAllSchedules_Success() throws Exception {
        // given
        studyScheduleRepository.save(StudySchedule.create(
                "제로투 발키리",
                "제로투 발키리와 함께하는 코딩세상",
                "플레이데이터",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                savedStudyRoom));
        studyScheduleRepository.save(StudySchedule.create(
                "제로투 발키리2",
                "제로투 발키리와 함께하는 코딩세상2",
                "플레이데이터",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1),
                savedStudyRoom));

        // when & then
        mockMvc.perform(get("/api/study-rooms/{studyRoomId}/schedules", savedStudyRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("참가한 스터디룸의 특정 스케줄을 조회합니다람쥐~~!")
    void findBySchedule_Success() throws Exception {
        // given
        StudySchedule savedSchedule = studyScheduleRepository.save(StudySchedule.create(
                "짜장면파티 일정",
                "나는 유니짜장",
                "송쉐프",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(2),
                savedStudyRoom));

        // when & then
        mockMvc.perform(get("/api/study-rooms/{studyRoomId}/schedules/{scheduleId}", savedStudyRoom.getId(), savedSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedSchedule.getId()))
                .andExpect(jsonPath("$.title").value("짜장면파티 일정"))
                .andDo(print());
    }
}