package com.wowraid.jobspoon.interview.service;


import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.account_project.service.AccountProjectService;
import com.wowraid.jobspoon.infrastructure.external.fastapi.client.FastApiEndInterview;
import com.wowraid.jobspoon.interview.controller.request.InterviewAccountProjectRequest;
import com.wowraid.jobspoon.interview.controller.request.InterviewEndRequest;
import com.wowraid.jobspoon.interview.controller.request.InterviewQARequest;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewEndRequestForm;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewProgressRequestForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.entity.InterviewType;
import com.wowraid.jobspoon.interview.repository.InterviewRepository;
import com.wowraid.jobspoon.interview.service.response.InterviewCreateResponse;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interview.service.strategy.interview_strategy.InterviewProcessStrategy;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.service.InterviewQAService;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.interviewee_profile.service.IntervieweeProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final AccountService accountService;
    private final IntervieweeProfileService intervieweeProfileService;
    private final InterviewQAService interviewQAService;
    private final InterviewRepository interviewRepository;
    private final ApplicationContext context;
    private final AccountProjectService accountProjectService;
    private final FastApiEndInterview fastApiEndInterview;

    @Value("${current_server.end_interview_url}")
    private String callbackUrl;


    @Override
    public InterviewCreateResponse createInterview(InterviewCreateRequestForm interviewCreateRequestForm, Long accountId, String userToken) {

        Account account = accountService.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("인터뷰 생성에서 account를 찾지 못함"));
        IntervieweeProfile intervieweeProfile = intervieweeProfileService.createIntervieweeProfile(interviewCreateRequestForm.toIntervieweeProfileRequest());
        Interview interview = interviewRepository.save(new Interview(account, intervieweeProfile,  interviewCreateRequestForm.getInterviewType()));
        InterviewQA interviewQA = interviewQAService.createInterviewQA(interviewCreateRequestForm.toInterviewQARequest(interview));
        InterviewProgressRequestForm interviewProgressRequestForm = new InterviewProgressRequestForm(interview.getId(), 1, interviewCreateRequestForm.getInterviewType(), interviewCreateRequestForm.getFirstAnswer(), interviewQA.getId());

        List<InterviewAccountProjectRequest> interviewAccountProjectRequests = interviewCreateRequestForm.getInterviewAccountProjectRequests();
        accountProjectService.saveAllByInterviewAccountProjectRequest(interviewAccountProjectRequests, account);

        InterviewProgressResponse interviewProgressResponse = execute(
                interviewCreateRequestForm.getInterviewType(),
                interviewProgressRequestForm,
                userToken
        );


        return interviewProgressResponse.toInterviewCreateResponse();


    }


    @Override
    public InterviewProgressResponse execute(InterviewType type, InterviewProgressRequestForm form, String userToken) {

        InterviewProcessStrategy strategy = context.getBean(String.valueOf(type), InterviewProcessStrategy.class);

        return strategy.process(form, userToken);
    }

    @Override
    public void endInterview(InterviewEndRequestForm interviewEndRequestForm, String userToken) {

        try {
            InterviewEndRequest endInterviewRequestEndInterviewRequest = createEndInterviewRequestEndInterviewRequest(interviewEndRequestForm, userToken);

            fastApiEndInterview.endInterview(endInterviewRequestEndInterviewRequest);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("인터뷰 종료 시 오류 발생");
        }



    }

    @Override
    public InterviewEndRequest createEndInterviewRequestEndInterviewRequest(InterviewEndRequestForm interviewEndRequestForm, String userToken) {
        Long interviewId = interviewEndRequestForm.getInterviewId();

        interviewQAService.saveInterviewAnswer(interviewEndRequestForm.getInterviewQAId(), interviewEndRequestForm.getAnswer());

        List<InterviewQA> allQA = interviewQAService.findAllByInterviewId(interviewEndRequestForm.getInterviewId());

        if (allQA.isEmpty()) {
            throw new IllegalArgumentException("인터뷰 종료 때 해당 인터뷰의 질문과 답변을 찾을 수 없습니다");
        }

        if (allQA.size() != 6) {
            throw new IllegalArgumentException("인터뷰 종료 때 인터뷰의 질문과 답변이 전부 존재 하지 않습니다");
        }

        List<String> questions = allQA.stream()
                .map(InterviewQA::getQuestion)
                .collect(Collectors.toList());

        List<String> answers = allQA.stream()
                .map(InterviewQA::getAnswer)
                .collect(Collectors.toList());


        return new InterviewEndRequest(
                userToken, interviewId, questions, answers, callbackUrl
        );
    }

    @Override
    public Optional<Interview> findById(Long id) {
        return interviewRepository.findById(id);
    }


}
