package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.account.repository.AccountRepository;
import com.wowraid.jobspoon.user_dashboard.entity.Interview;
import com.wowraid.jobspoon.user_dashboard.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public Interview createInterview(Long accountId, String topic, int experienceLevel, int projectExperience, int academicBackground, String techStack, String companyName) {
        if (!accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        Interview interview = new Interview(
                accountId, topic, experienceLevel,
                projectExperience, academicBackground,
                techStack, companyName
        );
        return interviewRepository.save(interview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Interview> listInterview(Long accountId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize); // ✅ 올바른 Pageable
        return interviewRepository.findByAccountId(accountId, pageable).getContent();
    }

    @Override
    @Transactional
    public void deleteInterview(Long accountId, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found"));
        if (!interview.getAccountId().equals(accountId)) {
            throw new SecurityException("Account does not own this interview");
        }
        interviewRepository.delete(interview);
    }
}
