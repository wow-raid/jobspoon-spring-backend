package com.wowraid.jobspoon.interview.service.strategy.sequence_strategy;

import com.wowraid.jobspoon.infrastructure.external.fastapi.client.FastApiFifthFollowupQuestionClientImpl;
import com.wowraid.jobspoon.infrastructure.external.fastapi.client.FastApiFourthFollowupQuestionClient;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiFourthProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.request.InterviewSequenceRequest;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.service.InterviewQAService;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.interviewee_profile.entity.TechStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("5")
@RequiredArgsConstructor
public class FifthQuestion implements InterviewSequenceStrategy {

    private final InterviewService interviewService;
    private final FastApiFifthFollowupQuestionClientImpl fastApiFifthFollowupQuestionClient;
    private final InterviewQAService interviewQAService;

    @Override
    public InterviewProgressResponse getQuestionByCompany(InterviewSequenceRequest interviewSequenceRequest, String userToken) {

        Interview interview = interviewService.findById(interviewSequenceRequest.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("다섯 번째 질문에서 인터뷰를 찾지 못했습니다"));

        IntervieweeProfile intervieweeProfile = interview.getIntervieweeProfile();

        List<TechStack> techStack = intervieweeProfile.getTechStack();

        Long interviewQAId = interviewSequenceRequest.getInterviewQAId();


        FastApiQuestionResponse fastApiFifthFollowupQuestion = fastApiFifthFollowupQuestionClient.getFastApiFifthFollowupQuestion(
                FastApiFourthProgressRequest.builder()
                        .interviewId(interview.getId())
                        .techStack(techStack)
                        .questionId(interviewQAId)
                        .answerText(interviewSequenceRequest.getAnswer())
                        .userToken(userToken)
                        .build()
        );

        String question = fastApiFifthFollowupQuestion.getQuestions().get(0);
        interviewQAService.saveInterviewAnswer(interviewQAId,interviewSequenceRequest.getAnswer());
        InterviewQA interviewQuestion = interviewQAService.createInterviewQuestion(interview, question);


        return new InterviewProgressResponse(
                interviewQuestion.getId(),
                interview.getId(),
                question
        );
    }


}
