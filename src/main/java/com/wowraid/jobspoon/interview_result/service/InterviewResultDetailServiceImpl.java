package com.wowraid.jobspoon.interview_result.service;


import com.wowraid.jobspoon.interview.controller.request_form.InterviewResultRequestForm;
import com.wowraid.jobspoon.interview_result.entity.InterviewResultDetail;
import com.wowraid.jobspoon.interview_result.repository.InterviewResultDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewResultDetailServiceImpl implements InterviewResultDetailService {

    private final InterviewResultDetailRepository interviewResultDetailRepository;


    @Override
    public List<InterviewResultDetail> createInterviewResultDetail(InterviewResultRequestForm interviewResultRequestForm) {

        Long interviewId = interviewResultRequestForm.getResult().getInterview_id();
        List<InterviewResultRequestForm.QaScore> qaScores = interviewResultRequestForm.getResult().getQa_scores();
        List<InterviewResultDetail> interviewResultDetails = new ArrayList<>();
        for (InterviewResultRequestForm.QaScore qaScore : qaScores) {
            InterviewResultDetail interviewResultDetail = new InterviewResultDetail(
                    interviewId,qaScore.getQuestion(),qaScore.getAnswer(),qaScore.getFeedback(),qaScore.getCorrection()
            );
            InterviewResultDetail savedInterviewResultDetail = interviewResultDetailRepository.save(interviewResultDetail);
            interviewResultDetails.add(savedInterviewResultDetail);
        }

        return interviewResultDetails;
    }
}
