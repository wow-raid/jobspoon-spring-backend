package com.wowraid.jobspoon.interview.service;


import com.wowraid.jobspoon.account.entity.Account;
import com.wowraid.jobspoon.account.service.AccountService;
import com.wowraid.jobspoon.interview.controller.request_form.InterviewCreateRequestForm;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final AccountService accountService;
    private final IntervieweeProfileService intervieweeProfileService;
    private final InterviewQAService interviewQAService;
    private final InterviewRepository interviewRepository;
    private final ApplicationContext context;



    @Override
    public InterviewCreateResponse createInterview(InterviewCreateRequestForm interviewCreateRequestForm, Long accountId, String userToken) {


        Account account = accountService.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("인터뷰 생성에서 account를 찾지 못함"));
        IntervieweeProfile intervieweeProfile = intervieweeProfileService.createIntervieweeProfile(interviewCreateRequestForm.toIntervieweeProfileRequest());
        Interview interview = interviewRepository.save(new Interview(account, intervieweeProfile,  interviewCreateRequestForm.getInterviewType()));
        InterviewQA interviewQA = interviewQAService.createInterviewQA(interviewCreateRequestForm.toInterviewQARequest(interview));
        InterviewProgressRequestForm interviewProgressRequestForm = new InterviewProgressRequestForm(interview.getId(), 1, interviewCreateRequestForm.getInterviewType(), interviewCreateRequestForm.getFirstAnswer(), interviewQA.getId());


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
    public Optional<Interview> findById(Long id) {
        return interviewRepository.findById(id);
    }


}
