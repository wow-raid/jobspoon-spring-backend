package com.wowraid.jobspoon.interview_result.service;

import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview_result.entity.InterviewResult;
import com.wowraid.jobspoon.interview_result.repository.InterviewResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InterviewResultServiceImpl implements InterviewResultService {

    private final InterviewResultRepository interviewResultRepository;

    private final InterviewService interviewService;

    public InterviewResultServiceImpl(
            InterviewResultRepository interviewResultRepository,
            @Lazy InterviewService interviewService
    ) {
        this.interviewResultRepository = interviewResultRepository;
        this.interviewService = interviewService;
    }


    @Override
    public InterviewResult createInterviewResult(InterviewResultRequestForm interviewResultRequestForm) {

        String overallComment = interviewResultRequestForm.getResult().getOverall_comment();
        Long interviewId = interviewResultRequestForm.getResult().getInterview_id();
        Interview interview = interviewService.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("인터뷰 결과 생성때 인터뷰를 찾을 수 없습니다."));


        return interviewResultRepository.save(
                new InterviewResult(interview, overallComment)
        );

    }
}
