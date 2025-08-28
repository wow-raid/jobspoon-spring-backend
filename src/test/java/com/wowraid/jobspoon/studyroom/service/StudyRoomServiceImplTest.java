package com.wowraid.jobspoon.studyroom.service;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.studyroom.controller.request_Form.CreateStudyRoomRequestForm;
import com.wowraid.jobspoon.studyroom.entity.StudyLevel;
import com.wowraid.jobspoon.studyroom.entity.StudyLocation;
import com.wowraid.jobspoon.studyroom.entity.StudyRoom;
import com.wowraid.jobspoon.studyroom.repository.StudyRoomRepository;
import com.wowraid.jobspoon.studyroom.service.request.ListStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.request.UpdateStudyRoomRequest;
import com.wowraid.jobspoon.studyroom.service.response.ListStudyRoomResponse;
import com.wowraid.jobspoon.studyroom.service.response.UpdateStudyRoomResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyRoomServiceImplTest {

    @MockBean
    private StudyRoomRepository studyRoomRepository;

    // AccountRepository도 필요하다면 @MockBean으로 등록
     @MockBean
     private AccountRepository accountRepository;

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
                Integer.valueOf(4),
                "ONLINE",
                "NEWBIE",
                List.of("백엔드"),
                List.of("Java")
        );

        // 👇 1. hostId 변수를 선언하고 임시 값을 할당합니다.
        final Long hostId = 1L;
        final Account fakeHost = new Account(hostId); // AccountRepository를 Mocking하기 위해 추가

        // 👇 2. AccountRepository의 동작을 정의합니다. (Service에서 필요로 함)
        when(accountRepository.findById(hostId)).thenReturn(Optional.of(fakeHost));

        when(studyRoomRepository.save(any(StudyRoom.class))).thenAnswer(invocation -> {
            StudyRoom studyRoomToSave = invocation.getArgument(0);
            ReflectionTestUtils.setField(studyRoomToSave, "id", 1L);
            return studyRoomToSave;
        });

        // when
        // 3. 서비스 메서드를 호출하고, 반환되는 StudyRoom 엔티티를 받습니다.
        StudyRoom resultStudyRoom = studyRoomService.createStudyRoom(requestForm, hostId);

        // then
        // 4. 반환된 엔티티의 필드 값들을 검증합니다.
        assertThat(resultStudyRoom).isNotNull();
        assertThat(resultStudyRoom.getId()).isEqualTo(1L);
        assertThat(resultStudyRoom.getTitle()).isEqualTo("테스트 제목");
        assertThat(resultStudyRoom.getDescription()).isEqualTo("테스트 설명");
        assertThat(resultStudyRoom.getMaxMembers()).isEqualTo(4);
        assertThat(resultStudyRoom.getLocation()).isEqualTo(StudyLocation.ONLINE);

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

    @Test
    @DisplayName("스터디룸 수정 서비스 테스트")
    void updateStudyRoom(){
        // given
        final Long studyRoomId = 1L;
        final Long hostId = 1L;
        final Account fakeHost = new Account(hostId);

        final UpdateStudyRoomRequest request = new UpdateStudyRoomRequest(
                "수정된 제목", "수정된 설명", 10,
                StudyLocation.BUSAN, StudyLevel.SENIOR,
                List.of("풀스택"), List.of("Kotlin")
        );

        // findById로 찾아올 원본 스터디모임 객체를 준비함
        StudyRoom originalStudyRoom = StudyRoom.create(
                fakeHost, "원본 제목", "원본 설명", 5,
                StudyLocation.SEOUL, StudyLevel.JUNIOR,
                List.of("백엔드"), List.of("Java")
        );
        ReflectionTestUtils.setField(originalStudyRoom, "id", studyRoomId);

        when(studyRoomRepository.findById(studyRoomId)).thenReturn(Optional.of(originalStudyRoom));
        when(accountRepository.findById(hostId)).thenReturn(Optional.of(fakeHost));

        // when
        UpdateStudyRoomResponse response = studyRoomService.updateStudyRoom(studyRoomId, request);

        // then
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getDescription()).isEqualTo("수정된 설명");
        assertThat(response.getLocation()).isEqualTo("BUSAN");
        assertThat(response.getSkillStack()).containsExactly("Kotlin");
    }

    @Test
    @DisplayName("스터디룸 삭제 서비스 테스트")
    void deleteStudyRoom(){
        // given
        final Long studyRoomId = 1L;
        final Long hostId = 1L;
        final Account fakeHost = new Account(hostId);

        StudyRoom fakeStudyRoom = StudyRoom.create(
                fakeHost, "삭제될 스터디", "설명", 5,
                StudyLocation.ONLINE, StudyLevel.ALL, null, null
        );

        when(studyRoomRepository.findById(studyRoomId)).thenReturn(Optional.of(fakeStudyRoom));

        // when
        studyRoomService.deleteStudyRoom(studyRoomId, hostId);

        // then
        verify(studyRoomRepository, times(1)).delete(fakeStudyRoom);
    }
}