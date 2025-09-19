package com.wowraid.jobspoon.user_dashboard.service;

import com.wowraid.jobspoon.user_dashboard.entity.InterviewResult;

import java.util.List;
import java.util.Map;

public interface InterviewResultService {
    InterviewResult saveInterviewResult(Long accountId, Long interviewId);

    InterviewResult getLastInterviewResult(Long accountId);

    List<Map<String, Object>> getFullQAList(Long interviewId);

    void saveQAScoreList(Long interviewResultId, String qaScoresJson);

    void recordHexagonEvaluation(Long interviewResultId, String evaluationScoresJson);
}
