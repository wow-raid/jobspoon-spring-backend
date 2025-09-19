package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.entity.InterviewResult;
import com.wowraid.jobspoon.user_dashboard.repository.InterviewResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InterviewResultServiceImpl implements InterviewResultService {

    private final InterviewResultRepository interviewResultRepository;

    @Override
    @Transactional
    public InterviewResult saveInterviewResult(Long accountId, Long interviewId) {
        InterviewResult result = new InterviewResult(
                null, accountId, interviewId,
                null, null, null,
                null
        );
        return interviewResultRepository.save(result);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResult getLastInterviewResult(Long accountId) {
        return interviewResultRepository.findTopByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @Override
    public List<Map<String, Object>> getFullQAList(Long interviewId) {
        // ⚠️ Spring에서는 InterviewQuestion/Answer 테이블도 필요
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public void saveQAScoreList(Long interviewResultId, String qaScoresJson) {
        InterviewResult result = interviewResultRepository.findById(interviewResultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));
        result.setQaList(qaScoresJson);
        interviewResultRepository.save(result);
    }

    @Override
    @Transactional
    public void recordHexagonEvaluation(Long interviewResultId, String evaluationScoresJson) {
        InterviewResult result = interviewResultRepository.findById(interviewResultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));
        result.setHexagonScore(evaluationScoresJson);
        interviewResultRepository.save(result);
    }
}
