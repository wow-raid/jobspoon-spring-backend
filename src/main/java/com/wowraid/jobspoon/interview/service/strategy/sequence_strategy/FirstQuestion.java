package com.wowraid.jobspoon.interview.service.strategy.sequence_strategy;

import com.wowraid.jobspoon.infrastructure.external.fastapi.client.FastApiFirstFollowupQuestionClient;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionRequest;
import com.wowraid.jobspoon.infrastructure.external.fastapi.response.FastApiFirstQuestionResponse;
import com.wowraid.jobspoon.interview.entity.AcademicBackground;
import com.wowraid.jobspoon.interview.entity.CompanyNameMapping;
import com.wowraid.jobspoon.interview.entity.ExperienceLevel;
import com.wowraid.jobspoon.interview.entity.Interview;
import com.wowraid.jobspoon.interview.entity.JobCategory;
import com.wowraid.jobspoon.interview.service.InterviewService;
import com.wowraid.jobspoon.interview.service.request.InterviewSequenceRequest;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interviewQA.entity.InterviewQA;
import com.wowraid.jobspoon.interviewQA.service.InterviewQAService;
import com.wowraid.jobspoon.interviewee_profile.entity.IntervieweeProfile;
import com.wowraid.jobspoon.interviewee_profile.service.IntervieweeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("1")
@RequiredArgsConstructor
public class FirstQuestion implements InterviewSequenceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(FirstQuestion.class);

    private final IntervieweeProfileService intervieweeProfileService;
    private final InterviewService interviewService;
    private final InterviewQAService interviewQAService;
    private final FastApiFirstFollowupQuestionClient fastApiFirstFollowupQuestionClient;

    @Override
    public InterviewProgressResponse getQuestionByCompany(InterviewSequenceRequest interviewSequenceRequest, String userToken) {

        IntervieweeProfile intervieweeProfile = intervieweeProfileService.findById(interviewSequenceRequest.getInterviewId())
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 ID의 인터뷰 정보를 찾을 수 없습니다 : " + interviewSequenceRequest.getInterviewId()));

        Interview interview = interviewService.findById(interviewSequenceRequest.getInterviewId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 인터뷰를 찾을 수 없습니다"));

        // Enum을 사용하여 문자열을 ID로 변환
        JobCategory jobCategory = JobCategory.fromString(intervieweeProfile.getJob());
        ExperienceLevel experienceLevel = ExperienceLevel.fromString(intervieweeProfile.getCareer());
        AcademicBackground academicBackground = AcademicBackground.fromString(intervieweeProfile.getMajor());
        
        // 회사 이름을 영문으로 변환
        String koreanCompanyName = intervieweeProfile.getCompany();
        String englishCompanyName = CompanyNameMapping.toEnglishName(koreanCompanyName);
        
        logger.info("회사 이름 변환: {} -> {}", koreanCompanyName, englishCompanyName);

        FastApiFirstQuestionResponse fastApiFirstFollowupQuestion = fastApiFirstFollowupQuestionClient.getFastApiFirstFollowupQuestion(
                new FastApiFirstQuestionRequest(
                        interviewSequenceRequest.getInterviewId(),
                        jobCategory.getId(),                // Enum의 ID 사용
                        experienceLevel.getId(),            // Enum의 ID 사용
                        academicBackground.getId(),         // Enum의 ID 사용
                        englishCompanyName,                 // 변환된 영문 회사 이름 사용
                        interviewSequenceRequest.getInterviewQAId(),
                        interviewSequenceRequest.getAnswer(),
                        userToken
                )
        );
        String question = fastApiFirstFollowupQuestion.getQuestions().get(0);
        InterviewQA interviewQuestion = interviewQAService.createInterviewQuestion(interview,question);
        interviewQAService.saveInterviewQAByInterview(interview, interviewQuestion);

        return new InterviewProgressResponse(interviewSequenceRequest.getInterviewQAId(), interviewSequenceRequest.getInterviewId(), question);
    }
}
