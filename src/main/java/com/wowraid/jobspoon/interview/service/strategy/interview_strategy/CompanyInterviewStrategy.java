package com.wowraid.jobspoon.interview.service.strategy.interview_strategy;


import com.wowraid.jobspoon.interview.controller.request_form.InterviewProgressRequestForm;
import com.wowraid.jobspoon.interview.service.response.InterviewProgressResponse;
import com.wowraid.jobspoon.interview.service.strategy.sequence_strategy.InterviewSequenceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("COMPANY")
public class CompanyInterviewStrategy implements InterviewProcessStrategy {

    private final ApplicationContext context;

    @Override
    public InterviewProgressResponse process(
            InterviewProgressRequestForm interviewProgressRequestForm, String userToken) {

        InterviewSequenceStrategy strategy = context.getBean(
                String.valueOf(interviewProgressRequestForm.getInterviewSequence()), InterviewSequenceStrategy.class);

        return strategy.getQuestionByCompany(interviewProgressRequestForm.toInterviewSequenceRequest(), userToken);

    }
}
