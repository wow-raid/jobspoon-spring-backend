package com.wowraid.jobspoon.interview.service.strategy.sequence_strategy;

import com.wowraid.jobspoon.account_project.entity.AccountProject;
import com.wowraid.jobspoon.account_project.service.AccountProjectService;
import com.wowraid.jobspoon.infrastructure.external.fastapi.client.FastApiSecondFollowupQuestionClient;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiSecondProgressRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiQuestionResponse;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.request.InterviewSequenceRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.request.FastApiAccountProjectRequest;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.service.InterviewQAService;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("2")
@RequiredArgsConstructor
public class SecondQuestion implements InterviewSequenceStrategy {

    private final InterviewService interviewService;
    private final AccountProjectService accountProjectService;
    private final RedisCacheService redisCacheService;
    private final FastApiSecondFollowupQuestionClient fastApiSecondFollowupQuestionClient;
    private final InterviewQAService interviewQAService;


    @Override
    public InterviewProgressResponse getQuestionByCompany(InterviewSequenceRequest interviewSequenceRequest, String userToken) {

        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        if (accountId == null) {
            new IllegalArgumentException("두 번째 질문에서 해당 유저를 찾을 수 없습니다");
        }
        log.info("");
        log.info("회원의 아이디 : {}",accountId);
        log.info("");

        Interview interview = interviewService.findById(interviewSequenceRequest.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("두 번째 질문에서 인터뷰 정보를 찾을 수 없습니다."));

        IntervieweeProfile intervieweeProfile = interview.getIntervieweeProfile();
        boolean projectExp = intervieweeProfile.isProjectExp();

        InterviewQA interviewQaByInterview = interviewQAService.createInterviewQaByInterview(interview);

        FastApiQuestionResponse fastApiSecondFollowupQuestion;

        if (projectExp) {
            List<AccountProject> allByAccountIdAndIsActiveTrue =
                    accountProjectService.findAllByAccount_IdAndIsActiveTrue(accountId);

            List<FastApiAccountProjectRequest> responseList = allByAccountIdAndIsActiveTrue.stream()
                    .map(project -> new FastApiAccountProjectRequest(
                            project.getProjectName(),
                            project.getProjectDescription()
                    ))
                    .collect(Collectors.toList());

            fastApiSecondFollowupQuestion = fastApiSecondFollowupQuestionClient.getFastApiSecondFollowupQuestion(
                    FastApiSecondProgressRequest.builder()
                            .userToken(userToken)
                            .interviewId(interview.getId())
                            .projectExperience(2)
                            .questionId(interviewQaByInterview.getId())
                            .projectResponses(responseList)
                            .build()
            );
        } else {
            fastApiSecondFollowupQuestion = fastApiSecondFollowupQuestionClient.getFastApiSecondFollowupQuestion(
                    FastApiSecondProgressRequest.builder()
                            .userToken(userToken)
                            .interviewId(interview.getId())
                            .projectExperience(1)
                            .questionId(interviewQaByInterview.getId())
                            .build()
            );
        }

        return new InterviewProgressResponse(
                interviewQaByInterview.getId(),
                interview.getId(),
                fastApiSecondFollowupQuestion.getQuestions().get(0)
        );
    }
}
