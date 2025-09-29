package com.wowraid.jobspoon.interview;

import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.interview.controller.request.InterviewQARequest;
import com.wowraid.jobspoon.interview.controller.request.IntervieweeProfileRequest;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.entity.TechStack;
import com.wowraid.jobspoon.interview.repository.InterviewRepository;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.InterviewServiceImpl;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.service.InterviewQAService;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.interviewee_profile.service.IntervieweeProfileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InterviewServiceTest {

    @InjectMocks
    private InterviewServiceImpl interviewService;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private IntervieweeProfileService intervieweeProfileService;
    @Mock
    private InterviewQAService interviewQAService;


    @Test
    @DisplayName("새로운 인터뷰가 생성됩니다")
    void 새로운_인터뷰_생성(){


        InterviewCreateRequestForm form = InterviewCreateRequestForm.builder()
                .company("당근마켓")
                .major("전공자")
                .career("3년 이하")
                .projectExp("있음")
                .job("Backend")
                .projectDescription("job-spoon 프로젝트는 ai 면접....")
                .techStacks(List.of(TechStack.BACKEND, TechStack.FULLSTACK))
                .firstQuestion("첫 질문 예시?")
                .firstAnswer("첫 답변 예시")
                .build();



        Account mockAccount = new Account(1L);

        IntervieweeProfile mockProfile = new IntervieweeProfile(
                "당근마켓", "전공자", "3년 이하", "있음", "Backend",
                "job-spoon 프로젝트는 ai 면접....", List.of(TechStack.BACKEND, TechStack.FULLSTACK)
        );

        InterviewQA mockInterviewQA = new InterviewQA(
                "자기소개 해주세요",
                "네 안녕하세요 ,,,,"
        );

        Interview interview = new Interview(mockAccount, mockInterviewQA, mockProfile);

        // given
        given(accountService.findById(any())).willReturn(Optional.of(mockAccount));

        given(intervieweeProfileService.createIntervieweeProfile(any(IntervieweeProfileRequest.class)))
                .willReturn(mockProfile);

        given(interviewQAService.createInterviewQA(any(InterviewQARequest.class)))
                .willReturn(mockInterviewQA);

        given(interviewRepository.save(any())).willReturn(interview);


        // when
        InterviewCreateResponse interviewCreateResponse = interviewService.createInterview(form, 1L);


        // then
        Assertions.assertNotNull(interviewCreateResponse);
        Assertions.assertEquals(interviewCreateResponse.getInterviewId(), interview.getId());


    }
}
