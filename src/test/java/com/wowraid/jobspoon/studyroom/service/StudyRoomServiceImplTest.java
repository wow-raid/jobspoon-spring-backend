package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class StudyRoomServiceImplTest {

    @MockBean
    private StudyRoomRepository studyRoomRepository;

    // AccountRepository도 필요하다면 @MockBean으로 등록
    // @MockBean
    // private AccountRepository accountRepository;

    @Autowired
    private StudyRoomService studyRoomService;

    @Test
    @DisplayName("스터디룸 생성 서비스 테스트")
    void createStudyRoom() {
        // given
        // 1. 서비스 메서드에 전달할 Form 객체를 생성합니다.
        final CreateStudyRoomRequestForm requestForm = new CreateStudyRoomRequestForm(
                "테스트 제목",
                "테스트 설명",
                4,
                "온라인",
                List.of("백엔드"),
                List.of("Java")
        );

        // 2. studyRoomRepository.save 메서드가 호출될 때의 동작을 정의합니다.
        //    어떤 StudyRoom 객체든 인자로 받으면, 그 객체를 그대로 반환하도록 설정합니다.
        when(studyRoomRepository.save(any(StudyRoom.class))).thenAnswer(invocation -> {
            StudyRoom studyRoomToSave = invocation.getArgument(0);
            // 실제 DB처럼 ID가 생성되었다고 가정하고 ID를 설정해줍니다.
            ReflectionTestUtils.setField(studyRoomToSave, "id", 1L);
            return studyRoomToSave;
        });

        // when
        // 3. 서비스 메서드를 호출하고, 반환되는 StudyRoom 엔티티를 받습니다.
        StudyRoom resultStudyRoom = studyRoomService.createStudyRoom(requestForm);

        // then
        // 4. 반환된 엔티티의 필드 값들을 검증합니다.
        assertThat(resultStudyRoom).isNotNull();
        assertThat(resultStudyRoom.getId()).isEqualTo(1L);
        assertThat(resultStudyRoom.getTitle()).isEqualTo("테스트 제목");
        assertThat(resultStudyRoom.getDescription()).isEqualTo("테스트 설명");
        assertThat(resultStudyRoom.getMaxMembers()).isEqualTo(4);
        assertThat(resultStudyRoom.getLocation()).isEqualTo(StudyLocation.온라인);

        // 5. studyRoomRepository의 save 메서드가 정확히 1번 호출되었는지 검증합니다.
        verify(studyRoomRepository).save(any(StudyRoom.class));
    }

    @Test
    @DisplayName("스터디룸 목록 조회 서비스 테스트 (첫 페이지)")
    void findAllStudyRooms_firstPage() {
        // given
        final ListStudyRoomRequest request = new ListStudyRoomRequest(null, 10);
        final Slice<StudyRoom> fakeResult = new SliceImpl<>(Collections.emptyList());

        // 👇 any()를 사용하여 어떤 PageRequest가 오든 fakeResult를 반환하도록 수정
        when(studyRoomRepository.findAllByOrderByIdDesc(any(PageRequest.class)))
                .thenReturn(fakeResult);

        // when
        ListStudyRoomResponse response = studyRoomService.findAllStudyRooms(request);

        // then
        assertThat(response).isNotNull(); // response가 null이 아닌지 확인
        assertThat(response.isHasNext()).isFalse();
    }
}