package com.wowraid.jobspoon.interviewQA.service;

import com.wowraid.jobspoon.interview.controller.request.InterviewQARequest;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.repository.InterviewQARepository;
import com.wowraid.jobspoon.interviewee_profile.repository.IntervieweeProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewQAServiceImpl implements InterviewQAService {

    private final InterviewQARepository interviewQARepository;

    @Override
    public InterviewQA createInterviewQA(InterviewQARequest interviewQARequest) {

        return interviewQARepository.save(
                new InterviewQA(
                        interviewQARequest.getFirstQuestion(),
                        interviewQARequest.getFirstAnswer()
                )
        );
    }
}
